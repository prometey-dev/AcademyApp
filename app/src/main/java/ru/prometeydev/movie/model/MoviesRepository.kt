package ru.prometeydev.movie.model

import androidx.paging.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.prometeydev.movie.model.database.MoviesDatabase
import ru.prometeydev.movie.model.database.entitiy.MoviesRemoteKeysEntity
import ru.prometeydev.movie.model.domain.Genre
import ru.prometeydev.movie.model.domain.Movie
import ru.prometeydev.movie.model.mappers.*
import ru.prometeydev.movie.model.network.MoviesApi
import ru.prometeydev.movie.ui.base.Result

class MoviesRepository(
    private val api: MoviesApi,
    private val db: MoviesDatabase
): BaseRepo() {

    private var baseImageUrl = ""
    private var genres: List<Genre> = emptyList()

    fun getMovieById(movieId: Int): Flow<Result<Movie>> = flow {
        emit(Result.Loading)

        withContext(dispatcher) {
            launch {
                if (baseImageUrl.isEmpty()) {
                    baseImageUrl = api.getConfiguration().images.secureBaseUrl
                }
            }

            val movie = db.moviesDao().getMovieById(movieId)
            if (movie.actors.isNullOrEmpty()) {
                val actors = api.getCredits(movieId).actors
                db.moviesDao().updateMovieWithActors(movieId, mapListActorsDtoToEntity(actors, baseImageUrl))
                db.moviesDao().insertActors(
                    actors = mapListActorsDtoToEntity(actors, baseImageUrl)
                )
                emit(
                    Result.Success(mapMoviesEntityToDomain(db.moviesDao().getMovieById(movieId)))
                )
            } else {
                emit(Result.Success(mapMoviesEntityToDomain(movie)))
            }
        }
    }.catch { e ->
        emit(Result.Error(e))
        emit(Result.Success(mapMoviesEntityToDomain(db.moviesDao().getMovieById(movieId))))
    }.flowOn(dispatcher)

    @ExperimentalPagingApi
    fun letMoviesFlowDb(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { db.moviesDao().getAllMovies() },
            remoteMediator = MoviesRemoteMediator(api, db)
        ).flow.map { pagingData ->
            pagingData.map { mapMoviesEntityToDomain(it) }
        }
    }

    suspend fun loadMoviesAndSave() = withContext(dispatcher) {

        launch {
            if (genres.isEmpty()) {
                genres = loadGenres()
            }
        }

        val pagesCount = db.keysDao().selectAll().size / PAGE_SIZE

        for (page in 1..pagesCount) {
            val response = api.getMoviesPopular(page)
            response.results.forEach { movie ->
                val isUpdated = db.moviesDao().updateRating(
                    movieId = movie.id,
                    numberOfRatings = movie.votesCount,
                    ratings = movie.ratings
                ) > 0

                if (!isUpdated) {
                    val key = MoviesRemoteKeysEntity(movieId = movie.id, prevKey = pagesCount, nextKey = pagesCount + 1)
                    db.keysDao().insert(key)
                    db.moviesDao().insertMovies(
                            movies = mapListMoviesDtoToEntity(response.results, genres, baseImageUrl)
                    )
                }
            }
        }
    }

    private suspend fun loadGenres(): List<Genre> {
        val data = api.getGenresList()
        val genres = mapListGenresDtoToEntity(data.genres)
        db.moviesDao().insertGenres(genres)
        return mapListGenresEntityToDomain(genres)
    }

    companion object {
        private const val PAGE_SIZE = 20
    }

}
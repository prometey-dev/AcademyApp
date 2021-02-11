package ru.prometeydev.movie.model

import androidx.paging.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import ru.prometeydev.movie.model.database.MoviesDatabase
import ru.prometeydev.movie.model.database.entitiy.MoviesRemoteKeysEntity
import ru.prometeydev.movie.model.domain.Genre
import ru.prometeydev.movie.model.domain.Movie
import ru.prometeydev.movie.model.mappers.*
import ru.prometeydev.movie.model.network.MoviesApi
import ru.prometeydev.movie.ui.MovieNotifications
import ru.prometeydev.movie.ui.base.Result

class MoviesRepository(
    private val api: MoviesApi,
    private val db: MoviesDatabase,
    private val notifications: MovieNotifications
): BaseRepo() {

    private var baseImageUrl = ""
    private var genres: List<Genre> = emptyList()
    private var topRatedMovie: Movie? = null

    init {
        notifications.initialize()
    }

    fun getMovieById(movieId: Int): Flow<Result<Movie>> = flow {
        emit(Result.Loading)

        withContext(dispatcher) {
            val loadImageConfig = async {
                if (baseImageUrl.isEmpty()) {
                    baseImageUrl = api.getConfiguration().images.secureBaseUrl
                }
            }

            val movie = db.moviesDao().getMovieById(movieId)
            if (movie.actors.isNullOrEmpty()) {
                val actors = api.getCredits(movieId).actors
                loadImageConfig.await()
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
            notifications.dismiss(movie.movieId)
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

        async {
            if (genres.isEmpty()) {
                genres = loadGenres()
            }
        }.await()

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

            response.results.maxByOrNull { it.ratings }?.let { movie ->
                if (movie.ratings > topRatedMovie?.ratings ?: 0f) {
                    topRatedMovie = mapMovieDtoToDomain(movie, genres, baseImageUrl)
                }
            }
        }

        topRatedMovie?.let { notifications.show(it) }
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
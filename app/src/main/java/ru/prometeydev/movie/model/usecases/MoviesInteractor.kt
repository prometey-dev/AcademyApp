package ru.prometeydev.movie.model.usecases

import androidx.paging.*
import androidx.room.withTransaction
import coil.annotation.ExperimentalCoilApi
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import ru.prometeydev.movie.model.database.entitiy.MoviesRemoteKeysEntity
import ru.prometeydev.movie.model.domain.Genre
import ru.prometeydev.movie.model.domain.Movie
import ru.prometeydev.movie.model.mappers.*
import ru.prometeydev.movie.model.usecases.MoviesRemoteMediator.Companion.STARTING_PAGE_INDEX
import ru.prometeydev.movie.model.usecases.base.BaseUseCases
import ru.prometeydev.movie.ui.base.Result

class MoviesInteractor: BaseUseCases() {

    private var baseImageUrl = ""
    private var genres: List<Genre> = emptyList()

    fun getMovieById(movieId: Int): Flow<Result<Movie>> = flow {
        emit(Result.Loading)

        withContext(dispatcher) {
            val jobBaseImageUrl = launch {
                if (baseImageUrl.isEmpty()) {
                    baseImageUrl = getImagesBaseUrl()
                }
            }
            var movieUri = ""
            val jobMovieUri = launch {
                movieUri = getWatchMovieUri(movieId)
            }

            val movie = repo.db.moviesDao().getMovieById(movieId)
            if (movie.actors.isNullOrEmpty()) {
                val actors = repo.api.getCredits(movieId).actors

                listOf(jobBaseImageUrl, jobMovieUri).joinAll()

                repo.db.moviesDao().updateMovieWithActors(movieId, mapListActorsDtoToEntity(actors, baseImageUrl))
                repo.db.moviesDao().updateMovieWithVideo(movieId, movieUri)
                repo.db.moviesDao().insertActors(
                    actors = mapListActorsDtoToEntity(actors, baseImageUrl)
                )
                emit(
                    Result.Success(mapMoviesEntityToDomain(repo.db.moviesDao().getMovieById(movieId)))
                )
            } else {
                emit(Result.Success(mapMoviesEntityToDomain(movie)))
            }
        }
    }.catch { e ->
        emit(Result.Error(e))
        emit(Result.Success(mapMoviesEntityToDomain(repo.db.moviesDao().getMovieById(movieId))))
    }.flowOn(dispatcher)

    @ExperimentalPagingApi
    fun letMoviesFlowDb(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { repo.db.moviesDao().getPagingSourceMovies() },
            remoteMediator = MoviesRemoteMediator(repo)
        ).flow.map { pagingData ->
            pagingData.map { mapMoviesEntityToDomain(it) }
        }
    }

    @ExperimentalPagingApi
    suspend fun loadMoviesPage(page: Int, loadType: LoadType): Boolean = withContext(dispatcher) {
        val jobBaseImageUrl = launch {
            if (baseImageUrl.isEmpty()) {
                baseImageUrl = getImagesBaseUrl()
            }
        }

        val jobGenres = launch {
            if (genres.isEmpty()) {
                genres = loadGenres()
            }
        }

        val response = repo.api.getMoviesPopular(page)

        val isEndOfList = response.page == response.pagesCount - 1
        repo.db.withTransaction {
            if (loadType == LoadType.REFRESH) {
                repo.db.keysDao().clearRemoteKeys()
                repo.db.moviesDao().clearAllMovies()
            }
            val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
            val nextKey = if (isEndOfList) null else page + 1
            val keys = response.results.map {
                MoviesRemoteKeysEntity(movieId = it.id, prevKey = prevKey, nextKey = nextKey)
            }
            listOf(jobBaseImageUrl, jobGenres).joinAll()
            repo.db.keysDao().insertAll(keys)
            repo.db.moviesDao().insertMovies(movies = mapListMoviesDtoToEntity(response.results, genres, baseImageUrl))
        }
        return@withContext isEndOfList
    }

    @ExperimentalCoilApi
    suspend fun loadMoviesAndSave() = withContext(dispatcher) {
        val jobBaseImageUrl = launch {
            if (baseImageUrl.isEmpty()) {
                baseImageUrl = getImagesBaseUrl()
            }
        }

        val jobGenres = launch {
            if (genres.isEmpty()) {
                genres = loadGenres()
            }
        }

        val pagesCount = repo.db.keysDao().selectAll().size / PAGE_SIZE

        listOf(jobBaseImageUrl, jobGenres).joinAll()
        for (page in 1..pagesCount) {
            val response = repo.api.getMoviesPopular(page)
            response.results.forEach { movie ->
                val isUpdated = repo.db.moviesDao().updateRating(
                    movieId = movie.id,
                    numberOfRatings = movie.votesCount,
                    ratings = movie.ratings
                ) > 0

                if (!isUpdated) {
                    val key = MoviesRemoteKeysEntity(movieId = movie.id, prevKey = pagesCount, nextKey = pagesCount + 1)
                    repo.db.keysDao().insert(key)
                    repo.db.moviesDao().insertMovies(
                            movies = mapListMoviesDtoToEntity(response.results, genres, baseImageUrl)
                    )
                }
            }
        }
    }

    private suspend fun getWatchMovieUri(movieId: Int): String {
        val videos = repo.api.getVideosByMovieId(movieId).results
        return if (videos.isNotEmpty()) {
            YOUTUBE_URI_WITHOUT_KEY + videos.first().key
        } else {
            ""
        }
    }

    private suspend fun getImagesBaseUrl(): String {
        return repo.api.getConfiguration().images.secureBaseUrl
    }

    private suspend fun loadGenres(): List<Genre> {
        val data = repo.api.getGenresList()
        val genres = mapListGenresDtoToEntity(data.genres)
        repo.db.moviesDao().insertGenres(genres)
        return mapListGenresEntityToDomain(genres)
    }

    companion object {
        private const val PAGE_SIZE = 20
        private const val YOUTUBE_URI_WITHOUT_KEY = "https://www.youtube.com/watch?v="
    }

}
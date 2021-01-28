package ru.prometeydev.movie.model

import androidx.paging.*
import kotlinx.coroutines.flow.*
import ru.prometeydev.movie.model.database.MoviesDatabase
import ru.prometeydev.movie.model.domain.Movie
import ru.prometeydev.movie.model.network.MoviesApi
import ru.prometeydev.movie.model.mappers.mapListActorsDtoToEntity
import ru.prometeydev.movie.model.mappers.mapMoviesEntityToDomain
import ru.prometeydev.movie.ui.base.Result

class MoviesRepository(
    private val api: MoviesApi,
    private val db: MoviesDatabase
): BaseRepo() {

    private var baseImageUrl = ""

    fun getMovieById(movieId: Int): Flow<Result<Movie>> = flow {
        emit(Result.Loading)

        if (baseImageUrl.isEmpty()) {
            baseImageUrl = api.getConfiguration().images.secureBaseUrl
        }
        val movie = db.moviesDao().getMovieById(movieId)
        if (movie.actors.isNullOrEmpty()) {
            val actors = api.getCredits(movieId).actors
            db.moviesDao().addActorsToTheMovie(movieId, mapListActorsDtoToEntity(actors, baseImageUrl))
            db.moviesDao().insertOrUpdateActors(
                    actors = mapListActorsDtoToEntity(actors, baseImageUrl)
            )
            emit(
                Result.Success(mapMoviesEntityToDomain(db.moviesDao().getMovieById(movieId)))
            )
        } else {
            emit(Result.Success(mapMoviesEntityToDomain(movie)))
        }
    }.catch { e ->
        emit(Result.Error(e))
        emit(Result.Success(mapMoviesEntityToDomain(db.moviesDao().getMovieById(movieId))))
    }.flowOn(dispatcher)

    //todo подумать как отсюда возвращать Flow<PagingData<Movie>> во ViewModel
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

    companion object {
        private const val PAGE_SIZE = 20
    }

}
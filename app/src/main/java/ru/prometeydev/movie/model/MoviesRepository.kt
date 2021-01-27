package ru.prometeydev.movie.model

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.prometeydev.movie.model.database.MoviesDatabase
import ru.prometeydev.movie.model.network.dto.*
import ru.prometeydev.movie.model.domain.Actor
import ru.prometeydev.movie.model.domain.Genre
import ru.prometeydev.movie.model.domain.Movie
import ru.prometeydev.movie.model.network.MoviesApi
import ru.prometeydev.movie.model.database.entitiy.ActorEntity
import ru.prometeydev.movie.model.database.entitiy.GenreEntity
import ru.prometeydev.movie.model.database.entitiy.MovieEntity
import ru.prometeydev.movie.model.mappers.mapListActorsDtoToEntity
import ru.prometeydev.movie.model.mappers.mapListGenresDtoToEntity
import ru.prometeydev.movie.model.mappers.mapListGenresEntityToDomain
import ru.prometeydev.movie.model.mappers.mapMoviesEntityToDomain

class MoviesRepository(
    private val api: MoviesApi,
    private val db: MoviesDatabase
): BaseRepo() {

    private var baseImageUrl = ""

    suspend fun getMovieById(movieId: Int): Movie = execute {
        if (baseImageUrl.isEmpty()) {
            baseImageUrl = getImageUrl()
        }

        val movie = db.moviesDao().getMovieById(movieId)
        if (movie.actors.isNullOrEmpty()) {
            val actors = api.getCredits(movieId).actors
            db.moviesDao().addActorsToTheMovie(movieId, mapListActorsDtoToEntity(actors, baseImageUrl))
            db.moviesDao().insertOrUpdateActors(
                    actors = mapListActorsDtoToEntity(actors, baseImageUrl)
            )
            mapMoviesEntityToDomain(db.moviesDao().getMovieById(movieId))
        } else {
            mapMoviesEntityToDomain(movie)
        }
    }

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

    private suspend fun getImageUrl(): String {
        return api.getConfiguration().images.secureBaseUrl
    }

    companion object {
        private const val PAGE_SIZE = 20
    }

}
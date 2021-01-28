package ru.prometeydev.movie.model

import androidx.paging.*
import androidx.room.withTransaction
import retrofit2.HttpException
import ru.prometeydev.movie.model.database.MoviesDatabase
import ru.prometeydev.movie.model.database.entitiy.MovieEntity
import ru.prometeydev.movie.model.database.entitiy.MoviesRemoteKeysEntity
import ru.prometeydev.movie.model.domain.Genre
import ru.prometeydev.movie.model.mappers.mapListGenresDtoToEntity
import ru.prometeydev.movie.model.mappers.mapListGenresEntityToDomain
import ru.prometeydev.movie.model.mappers.mapListMoviesDtoToEntity
import ru.prometeydev.movie.model.network.MoviesApi
import java.io.IOException
import java.io.InvalidObjectException

//todo немного отрефакторить
@ExperimentalPagingApi
class MoviesRemoteMediator(
    private val api: MoviesApi,
    private val db: MoviesDatabase
) : RemoteMediator<Int, MovieEntity>() {

    private var baseImageUrl = ""

    private var genres: List<Genre> = emptyList()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, MovieEntity>): MediatorResult {
        val page = when (val pageKeyData = getKeyPageData(loadType, state)) {
            is MediatorResult.Success -> return pageKeyData
            else -> pageKeyData as Int
        }

        return try {
            if (baseImageUrl.isEmpty()) {
                baseImageUrl = api.getConfiguration().images.secureBaseUrl
            }
            if (genres.isEmpty()) {
                genres = loadGenres()
            }
            val response = api.getMoviesPopular(page)

            val isEndOfList = response.page == response.pagesCount - 1
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.keysDao().clearRemoteKeys()
                    db.moviesDao().clearAllMovies()
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1
                val keys = response.results.map {
                    MoviesRemoteKeysEntity(movieId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                db.keysDao().insertAll(keys)
                db.moviesDao().insertOrUpdateMovies(
                        movies = mapListMoviesDtoToEntity(response.results, genres, baseImageUrl)
                )
            }
            return MediatorResult.Success(endOfPaginationReached = isEndOfList)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }

    }

    private suspend fun loadGenres(): List<Genre> {
        val data = api.getGenresList()
        val genres = mapListGenresDtoToEntity(data.genres)
        db.moviesDao().insertOrUpdateGenres(genres)
        return mapListGenresEntityToDomain(genres)
    }

    private suspend fun getKeyPageData(loadType: LoadType, state: PagingState<Int, MovieEntity>): Any? {
        return when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getClosestRemoteKey(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                        ?: throw InvalidObjectException("Remote key should not be null for $loadType")
                remoteKeys.nextKey
            }
            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
                        ?: throw InvalidObjectException("Invalid state, key should not be null")
                remoteKeys.prevKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                remoteKeys.prevKey
            }
        }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, MovieEntity>): MoviesRemoteKeysEntity? {
        return state.pages
                .lastOrNull { it.data.isNotEmpty() }
                ?.data?.lastOrNull()
                ?.let { db.keysDao().remoteKeysByMovieId(it.movieId) }
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, MovieEntity>): MoviesRemoteKeysEntity? {
        return state.pages
                .firstOrNull { it.data.isNotEmpty() }
                ?.data?.firstOrNull()
                ?.let { db.keysDao().remoteKeysByMovieId(it.movieId) }
    }

    private suspend fun getClosestRemoteKey(state: PagingState<Int, MovieEntity>): MoviesRemoteKeysEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.movieId?.let { movieId ->
                db.keysDao().remoteKeysByMovieId(movieId)
            }
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }

}
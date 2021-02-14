package ru.prometeydev.movie.model.usecases

import androidx.paging.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.prometeydev.movie.model.Repo
import ru.prometeydev.movie.model.database.entitiy.MovieEntity
import ru.prometeydev.movie.model.database.entitiy.MoviesRemoteKeysEntity
import java.io.InvalidObjectException

@ExperimentalPagingApi
class MoviesRemoteMediator(private val repo: Repo) : RemoteMediator<Int, MovieEntity>(), KoinComponent {

    private val interactor: MoviesInteractor by inject()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, MovieEntity>): MediatorResult {
        val page = when (val pageKeyData = getKeyPageData(loadType, state)) {
            is MediatorResult.Success -> return pageKeyData
            else -> pageKeyData as Int
        }

        return try {
            val isEndOfList = interactor.loadMoviesPage(page, loadType)
            return MediatorResult.Success(endOfPaginationReached = isEndOfList)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }

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
                ?.let { repo.db.keysDao().getKeysByMovieId(it.movieId) }
    }

    private suspend fun getFirstRemoteKey(state: PagingState<Int, MovieEntity>): MoviesRemoteKeysEntity? {
        return state.pages
                .firstOrNull { it.data.isNotEmpty() }
                ?.data?.firstOrNull()
                ?.let { repo.db.keysDao().getKeysByMovieId(it.movieId) }
    }

    private suspend fun getClosestRemoteKey(state: PagingState<Int, MovieEntity>): MoviesRemoteKeysEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.movieId?.let { movieId ->
                repo.db.keysDao().getKeysByMovieId(movieId)
            }
        }
    }

    companion object {
        const val STARTING_PAGE_INDEX = 1
    }

}
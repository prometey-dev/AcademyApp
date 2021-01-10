package ru.prometeydev.movie.model

import androidx.paging.PagingSource
import retrofit2.HttpException
import ru.prometeydev.movie.model.local.Movie
import java.io.IOException
import java.lang.Exception

class MoviesPagingSource(
    private val repository: MoviesRepository
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: STARTING_PAGE_INDEX
        return try {
            val response = repository.loadMovies(page)
            LoadResult.Page(
                data = repository.mapMovies(response.results),
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (page == response.pagesCount - 1) null else page + 1
            )
        } catch (ex: Throwable) {
            LoadResult.Error(ex)
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }

}
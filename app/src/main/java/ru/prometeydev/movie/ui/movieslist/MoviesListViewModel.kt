package ru.prometeydev.movie.ui.movieslist

import androidx.lifecycle.viewModelScope
import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.prometeydev.movie.model.MoviesPagingSource
import ru.prometeydev.movie.model.local.Movie
import ru.prometeydev.movie.model.MoviesRepository
import ru.prometeydev.movie.ui.base.BaseViewModel
import ru.prometeydev.movie.ui.base.Result

class MoviesListViewModel(
    private val repository: MoviesRepository
): BaseViewModel<Result>() {

    private var currentMoviesResult: Flow<PagingData<Movie>>? = null

    private val _state = MutableStateFlow<Result>(Result.Loading)
    val state: StateFlow<Result> get() = _state

    fun loadMovies() {
        val lastResult = currentMoviesResult
        if (lastResult != null) {
            return
        }

        requestWithStateFlow(_state) {
            val newResult: Flow<PagingData<Movie>> = Pager(
                    config = PagingConfig(pageSize = 20, enablePlaceholders = false),
                    pagingSourceFactory = { MoviesPagingSource(repository) }
            ).flow.cachedIn(viewModelScope)
            currentMoviesResult = newResult
            newResult
        }
    }

}
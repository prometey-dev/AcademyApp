package ru.prometeydev.movie.ui.movieslist

import androidx.lifecycle.viewModelScope
import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.prometeydev.movie.model.domain.Movie
import ru.prometeydev.movie.model.MoviesRepository
import ru.prometeydev.movie.ui.base.BaseViewModel
import ru.prometeydev.movie.ui.base.Result

class MoviesListViewModel(
    private val repository: MoviesRepository
): BaseViewModel<PagingData<Movie>>() {

    private var currentMoviesResult: Flow<PagingData<Movie>>? = null

    val stateFlow: StateFlow<Result<PagingData<Movie>>> get() = mutableStateFlow

    @ExperimentalPagingApi
    fun loadMovies() {
        val lastResult = currentMoviesResult
        if (lastResult != null) {
            return
        }

        requestWithStateFlow {
            val newResult = repository.letMoviesFlowDb().cachedIn(viewModelScope)
            currentMoviesResult = newResult
            newResult
        }
    }

}
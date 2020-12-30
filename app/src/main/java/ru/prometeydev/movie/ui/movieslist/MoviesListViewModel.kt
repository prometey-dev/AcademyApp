package ru.prometeydev.movie.ui.movieslist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.prometeydev.movie.model.Movie
import ru.prometeydev.movie.model.MoviesRepository
import ru.prometeydev.movie.ui.base.BaseViewModel
import ru.prometeydev.movie.ui.base.StateLoading

class MoviesListViewModel(
    private val repository: MoviesRepository
): BaseViewModel() {

    private val _mutableMoviesListState = MutableLiveData<List<Movie>>(emptyList())

    val moviesListState: LiveData<List<Movie>> get() = _mutableMoviesListState

    fun loadMovies() {
        if (_mutableMoviesListState.value.isNullOrEmpty()) {
            viewModelScope.launch(exceptionHandler()) {
                mutableStateLoading.value = StateLoading.Loading

                _mutableMoviesListState.value = repository.loadMovies()
                mutableError.value = null
                mutableStateLoading.value = StateLoading.Success
            }
        }
    }

}
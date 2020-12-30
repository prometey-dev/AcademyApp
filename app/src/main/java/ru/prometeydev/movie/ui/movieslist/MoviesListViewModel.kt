package ru.prometeydev.movie.ui.movieslist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.prometeydev.movie.model.Movie
import ru.prometeydev.movie.model.MoviesRepository

class MoviesListViewModel(
    private val repository: MoviesRepository
): ViewModel() {

    private val _mutableMoviesListState = MutableLiveData<List<Movie>>(emptyList())

    val moviesListState: LiveData<List<Movie>> get() = _mutableMoviesListState

    fun updateMovies() {
        if (_mutableMoviesListState.value.isNullOrEmpty()) {
            viewModelScope.launch {
                _mutableMoviesListState.value = repository.loadMovies()
            }
        }
    }

}
package ru.prometeydev.movie.ui.movieslist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.prometeydev.movie.data.Movie
import ru.prometeydev.movie.data.MoviesRepository

class MoviesListViewModel(
    private val repository: MoviesRepository
): ViewModel() {

    private val _mutableMoviesListState = MutableLiveData<List<Movie>>(emptyList())

    val moviesListState: LiveData<List<Movie>> get() = _mutableMoviesListState

    fun updateMovies() {
        viewModelScope.launch {
            _mutableMoviesListState.value = repository.loadMovies()
        }
    }

}
package ru.prometeydev.movie.ui.movieslist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.prometeydev.movie.data.Movie
import ru.prometeydev.movie.data.loadMovies

class MoviesListViewModel: ViewModel() {

    private val _mutableMoviesListState = MutableLiveData<List<Movie>>(emptyList())

    val moviesListState: LiveData<List<Movie>> get() = _mutableMoviesListState

    fun updateMovies(context: Context) {
        viewModelScope.launch {
            _mutableMoviesListState.value = loadMovies(context)
        }
    }

}
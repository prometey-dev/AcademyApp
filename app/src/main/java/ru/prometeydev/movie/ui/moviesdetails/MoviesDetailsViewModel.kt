package ru.prometeydev.movie.ui.moviesdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.prometeydev.movie.data.Movie
import ru.prometeydev.movie.data.MoviesRepository

class MoviesDetailsViewModel(
    private val repository: MoviesRepository
) : ViewModel() {

    private val _mutableMovieState = MutableLiveData<Movie>()

    val movieState: LiveData<Movie> get() = _mutableMovieState

    fun updateMovie(id: Int) {
        viewModelScope.launch {
            _mutableMovieState.value = repository.getMovieById(id)
        }
    }

}
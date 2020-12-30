package ru.prometeydev.movie.ui.moviesdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.prometeydev.movie.model.Movie
import ru.prometeydev.movie.model.MovieDetails
import ru.prometeydev.movie.model.MoviesRepository

class MoviesDetailsViewModel(
    private val repository: MoviesRepository
) : ViewModel() {

    private val _mutableMovieState = MutableLiveData<MovieDetails>()

    val movieState: LiveData<MovieDetails> get() = _mutableMovieState

    fun updateMovie(id: Int) {
        if (_mutableMovieState.value?.id != id) {
            viewModelScope.launch {
                _mutableMovieState.value = repository.getMovieById(id)
            }
        }
    }

}
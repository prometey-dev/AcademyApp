package ru.prometeydev.movie.ui.moviesdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.prometeydev.movie.model.MovieDetails
import ru.prometeydev.movie.model.MoviesRepository
import ru.prometeydev.movie.ui.base.BaseViewModel
import ru.prometeydev.movie.ui.base.StateLoading

class MoviesDetailsViewModel(
    private val repository: MoviesRepository
) : BaseViewModel() {

    private val _mutableMovieState = MutableLiveData<MovieDetails>()

    val movieState: LiveData<MovieDetails> get() = _mutableMovieState

    fun loadMovie(movieId: Int) {
        if (_mutableMovieState.value?.id != movieId) {
            viewModelScope.launch(exceptionHandler()) {
                mutableStateLoading.value = StateLoading.Loading

                _mutableMovieState.value = repository.getMovieById(movieId)
                mutableError.value = null
                mutableStateLoading.value = StateLoading.Success
            }
        }
    }

}
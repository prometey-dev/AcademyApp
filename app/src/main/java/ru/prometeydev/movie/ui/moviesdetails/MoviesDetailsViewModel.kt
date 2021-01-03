package ru.prometeydev.movie.ui.moviesdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.prometeydev.movie.model.local.MovieDetails
import ru.prometeydev.movie.model.MoviesRepository
import ru.prometeydev.movie.ui.base.BaseViewModel
import ru.prometeydev.movie.ui.base.Event

class MoviesDetailsViewModel(
    private val repository: MoviesRepository
) : BaseViewModel() {

    private val _mutableLiveData = MutableLiveData<Event<MovieDetails>>()

    val liveData: LiveData<Event<MovieDetails>> get() = _mutableLiveData

    fun loadMovie(movieId: Int) {
        if (_mutableLiveData.value?.data?.id != movieId) {
            requestWithLiveData(_mutableLiveData) {
                repository.getMovieById(movieId)
            }
        }
    }

}
package ru.prometeydev.movie.ui.moviesdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.prometeydev.movie.model.local.MovieDetails
import ru.prometeydev.movie.model.MoviesRepository
import ru.prometeydev.movie.ui.base.BaseViewModel
import ru.prometeydev.movie.ui.base.Result

class MoviesDetailsViewModel(
    private val repository: MoviesRepository
) : BaseViewModel() {

    private val _mutableLiveData = MutableLiveData<Result>()

    val liveData: LiveData<Result> get() = _mutableLiveData

    @Suppress("UNCHECKED_CAST")
    fun loadMovie(movieId: Int) {
            requestWithLiveData(_mutableLiveData) {
                repository.getMovieById(movieId)
            }
    }

}
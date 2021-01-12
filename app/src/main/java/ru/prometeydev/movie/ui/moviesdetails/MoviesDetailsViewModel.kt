package ru.prometeydev.movie.ui.moviesdetails

import androidx.lifecycle.LiveData
import ru.prometeydev.movie.model.MoviesRepository
import ru.prometeydev.movie.model.local.MovieDetails
import ru.prometeydev.movie.ui.base.BaseViewModel
import ru.prometeydev.movie.ui.base.Result

class MoviesDetailsViewModel(
    private val repository: MoviesRepository
) : BaseViewModel<MovieDetails>() {

    val liveData: LiveData<Result<MovieDetails>> get() = mutableLiveData

    fun loadMovie(movieId: Int) {
        requestWithLiveData {
            repository.getMovieById(movieId)
        }
    }

}
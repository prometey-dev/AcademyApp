package ru.prometeydev.movie.ui.moviesdetails

import kotlinx.coroutines.flow.*
import ru.prometeydev.movie.model.MoviesRepository
import ru.prometeydev.movie.model.local.MovieDetails
import ru.prometeydev.movie.ui.base.BaseViewModel
import ru.prometeydev.movie.ui.base.Result

class MoviesDetailsViewModel(
    private val repository: MoviesRepository
) : BaseViewModel<MovieDetails>() {

    val stateFlow: StateFlow<Result<MovieDetails>> get() = mutableStateFlow

    fun loadMovie(movieId: Int) {
        requestWithStateFlow {
            flow {
                val movie = repository.getMovieById(movieId)
                emit(movie)
            }
        }
    }

}
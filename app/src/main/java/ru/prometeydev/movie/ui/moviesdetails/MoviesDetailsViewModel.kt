package ru.prometeydev.movie.ui.moviesdetails

import kotlinx.coroutines.flow.*
import ru.prometeydev.movie.model.MoviesRepository
import ru.prometeydev.movie.model.domain.Movie
import ru.prometeydev.movie.ui.base.BaseViewModel
import ru.prometeydev.movie.ui.base.Result

class MoviesDetailsViewModel(
    private val repository: MoviesRepository
) : BaseViewModel<Movie>() {

    val stateFlow: StateFlow<Result<Movie>> get() = mutableStateFlow

    fun loadMovie(movieId: Int) {
        requestWithStateFlowFromResult {
            repository.getMovieById(movieId)
        }
    }

}
package ru.prometeydev.movie.ui.moviesdetails

import kotlinx.coroutines.flow.*
import ru.prometeydev.movie.model.usecases.MoviesInteractor
import ru.prometeydev.movie.model.domain.Movie
import ru.prometeydev.movie.ui.MovieNotifications
import ru.prometeydev.movie.ui.base.BaseViewModel
import ru.prometeydev.movie.ui.base.Result

class MoviesDetailsViewModel(
        private val interactor: MoviesInteractor,
        private val notifications: MovieNotifications
) : BaseViewModel<Movie>() {

    val stateFlow: StateFlow<Result<Movie>> get() = mutableStateFlow

    fun loadMovie(movieId: Int) {
        notifications.dismiss(movieId)
        requestWithStateFlowFromResult {
            interactor.getMovieById(movieId)
        }
    }

}
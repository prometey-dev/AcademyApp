package ru.prometeydev.movie.ui.movieslist

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import ru.prometeydev.movie.common.show
import ru.prometeydev.movie.data.Movie
import ru.prometeydev.movie.ui.moviesdetails.MoviesDetailsFragment

open class MoviesListNavigable<VM: ViewModel> : Fragment() {

    fun openMovieDetails(movie: Movie) {
        show(MoviesDetailsFragment.instance(movie))
    }

}
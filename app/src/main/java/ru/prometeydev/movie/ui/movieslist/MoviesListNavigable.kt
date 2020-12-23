package ru.prometeydev.movie.ui.movieslist

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.prometeydev.movie.ViewModelProviderFactory
import ru.prometeydev.movie.common.show
import ru.prometeydev.movie.data.Movie
import ru.prometeydev.movie.ui.moviesdetails.MoviesDetailsFragment

open class MoviesListNavigable : Fragment() {

    protected val viewModel: MoviesListViewModel by viewModels { ViewModelProviderFactory() }

    fun openMovieDetails(movie: Movie) {
        show(MoviesDetailsFragment.instance(movie))
    }

}
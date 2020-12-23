package ru.prometeydev.movie.ui.moviesdetails

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.prometeydev.movie.ViewModelProviderFactory
import ru.prometeydev.movie.common.popBack
import ru.prometeydev.movie.common.toast

open class MoviesDetailsNavigable : Fragment() {

    protected val viewModel: MoviesDetailsViewModel by viewModels { ViewModelProviderFactory() }

    fun goBack() = popBack()

    fun showMessage(message: String) = toast(message)

}
package ru.prometeydev.movie.ui.moviesdetails

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import ru.prometeydev.movie.common.popBack
import ru.prometeydev.movie.common.toast

open class MoviesDetailsNavigable<VM: ViewModel> : Fragment() {

    fun goBack() = popBack()

    fun showMessage(message: String) = toast(message)

}
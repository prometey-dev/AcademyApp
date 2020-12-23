package ru.prometeydev.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.prometeydev.movie.ui.moviesdetails.MoviesDetailsViewModel
import ru.prometeydev.movie.ui.movieslist.MoviesListViewModel
import java.lang.IllegalArgumentException

class ViewModelProviderFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = when (modelClass) {
        MoviesListViewModel::class.java -> MoviesListViewModel()
        MoviesDetailsViewModel::class.java -> MoviesDetailsViewModel()
        else -> throw IllegalArgumentException("$modelClass is not registered ViewModel")
    } as T

}
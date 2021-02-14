package ru.prometeydev.movie.di

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.prometeydev.movie.ui.moviesdetails.MoviesDetailsViewModel
import ru.prometeydev.movie.ui.movieslist.MoviesListViewModel

val viewModelModule = module {
    viewModel { MoviesListViewModel(interactor = get()) }
    viewModel { MoviesDetailsViewModel(interactor = get(), notifications = get()) }
}
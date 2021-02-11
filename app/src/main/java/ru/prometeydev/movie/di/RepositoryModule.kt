package ru.prometeydev.movie.di

import org.koin.dsl.module
import ru.prometeydev.movie.model.MoviesRepository
import ru.prometeydev.movie.ui.MovieNotifications

val repoModule = module {
    single { MoviesRepository(api = get(), db = get(), notifications = get()) }
    single { MovieNotifications(context = get()) }
}
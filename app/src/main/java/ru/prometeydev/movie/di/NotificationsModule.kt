package ru.prometeydev.movie.di

import org.koin.dsl.module
import ru.prometeydev.movie.ui.MovieNotifications

val notifyModule = module {
    single { MovieNotifications(context = get()) }
}
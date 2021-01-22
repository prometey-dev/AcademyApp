package ru.prometeydev.movie.di

import org.koin.dsl.module
import ru.prometeydev.movie.model.MoviesRepository

val repoModule = module {
    single { MoviesRepository(api = get(), dao = get()) }
}
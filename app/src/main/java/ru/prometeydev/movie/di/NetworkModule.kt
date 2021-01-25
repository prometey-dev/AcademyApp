package ru.prometeydev.movie.di

import org.koin.dsl.module
import ru.prometeydev.movie.model.network.MoviesApiProvider

val networkModule = module {
    single { MoviesApiProvider() }
    single(createdAtStart = false) { get<MoviesApiProvider>().api }
}
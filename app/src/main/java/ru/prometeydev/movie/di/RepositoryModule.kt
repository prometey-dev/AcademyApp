package ru.prometeydev.movie.di

import org.koin.dsl.module
import ru.prometeydev.movie.model.Repo
import ru.prometeydev.movie.model.usecases.MoviesInteractor
import ru.prometeydev.movie.model.usecases.TopRatedMovieInteractor

val repoModule = module {
    single { Repo(api = get(), db = get()) }
    single { MoviesInteractor() }
    single { TopRatedMovieInteractor() }
}
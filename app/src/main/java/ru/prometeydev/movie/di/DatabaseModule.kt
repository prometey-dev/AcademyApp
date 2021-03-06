package ru.prometeydev.movie.di

import org.koin.dsl.module
import ru.prometeydev.movie.model.database.MoviesDatabase

val databaseModule = module {
    single { MoviesDatabase(context = get()) }
    single(createdAtStart = false) { get<MoviesDatabase>().moviesDao() }
}
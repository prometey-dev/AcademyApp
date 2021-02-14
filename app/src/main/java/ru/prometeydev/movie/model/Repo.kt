package ru.prometeydev.movie.model

import ru.prometeydev.movie.model.database.MoviesDatabase
import ru.prometeydev.movie.model.network.MoviesApi

class Repo(val api: MoviesApi, val db: MoviesDatabase)
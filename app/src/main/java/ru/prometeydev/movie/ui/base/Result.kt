package ru.prometeydev.movie.ui.base

sealed class Result {
    object Loading : Result()
    data class Success<out T>(val data: T?) : Result()
    data class Error(val message: String?) : Result()
}


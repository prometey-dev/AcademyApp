package ru.prometeydev.movie.ui.base

sealed class StateLoading {
    object Default : StateLoading()
    object Loading : StateLoading()
    object Success : StateLoading()
    object Error : StateLoading()
}

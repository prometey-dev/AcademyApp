package ru.prometeydev.movie.ui.base

interface CanHandleNavigation {
    fun navigateTo(step: String)
    fun goBack()
}
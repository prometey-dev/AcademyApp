package ru.prometeydev.movie.common

import androidx.fragment.app.Fragment
import ru.prometeydev.movie.FragmentMoviesList
import ru.prometeydev.movie.ui.Screen
import java.lang.IllegalStateException

fun String.asFragmentScreen(): Fragment {
    return when (this) {
        Screen.MOVIES_LIST -> FragmentMoviesList.instance(this)
        else -> throw IllegalStateException(this)
    }
}
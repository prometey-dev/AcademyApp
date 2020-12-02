package ru.prometeydev.movie.data.domain

import ru.prometeydev.movie.R
import ru.prometeydev.movie.data.models.Actor

class ActorsDataSource {
    fun getActors(): List<Actor> = listOf(
        Actor("Robert Downey Jr.", R.drawable.actor1),
        Actor("Chris Evans", R.drawable.actor2),
        Actor("Mark Ruffalo", R.drawable.actor3),
        Actor("Chris Hemsworth", R.drawable.actor4)
    )
}
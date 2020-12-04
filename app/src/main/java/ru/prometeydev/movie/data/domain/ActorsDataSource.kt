package ru.prometeydev.movie.data.domain

import ru.prometeydev.movie.R
import ru.prometeydev.movie.data.models.Actor

class ActorsDataSource {
    fun getActors(): ArrayList<Actor> = arrayListOf(
        Actor(1,"Robert Downey Jr.", R.drawable.actor1),
        Actor(2, "Chris Evans", R.drawable.actor2),
        Actor(3, "Mark Ruffalo", R.drawable.actor3),
        Actor(4, "Chris Hemsworth", R.drawable.actor4)
    )
}
package ru.prometeydev.movie.model.domain

data class Movie(
        val id: Int,
        val title: String,
        val overview: String?,
        val poster: String?,
        val backdrop: String?,
        val numberOfRatings: Int,
        val minimumAge: Int,
        val ratings: Float,
        val adult: Boolean,
        val runtime: Int? = null,
        val genres: List<Genre> = emptyList(),
        val actors: List<Actor> = emptyList(),
        val video: String? = null
)
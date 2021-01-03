package ru.prometeydev.movie.model.local

data class Movie(
        val id: Int,
        val title: String,
        val overview: String,
        val poster: String?,
        val backdrop: String?,
        val numberOfRatings: Int,
        val minimumAge: Int,
        val ratings: Float,
        val adult: Boolean,
        val genres: List<Genre>,
)
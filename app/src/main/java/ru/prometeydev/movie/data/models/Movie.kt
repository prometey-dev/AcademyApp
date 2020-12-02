package ru.prometeydev.movie.data.models

data class Movie(
    val name: String,
    val genre: String,
    val duration: Int,
    val rating: Float,
    val reviewsCount: Int,
    val ageLimit: Int,
    val filmCoverDrawable: Int,
    val hasLike: Boolean
)
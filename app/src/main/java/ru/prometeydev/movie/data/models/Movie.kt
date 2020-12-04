package ru.prometeydev.movie.data.models

import androidx.annotation.DrawableRes

data class Movie(
    val id: Long,
    val name: String,
    val genre: String,
    val duration: Int = 0,
    val rating: Float,
    val reviewsCount: Int,
    val ageLimit: Int,
    @DrawableRes
    val filmCoverDrawable: Int,
    val hasLike: Boolean = false,
    val additional: MovieAdditional? = null
)
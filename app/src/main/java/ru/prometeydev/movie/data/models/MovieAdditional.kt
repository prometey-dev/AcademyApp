package ru.prometeydev.movie.data.models

import androidx.annotation.DrawableRes

data class MovieAdditional(
    @DrawableRes
    val pictureDrawable: Int,
    val storyLine: String,
    val actors: ArrayList<Actor>
)
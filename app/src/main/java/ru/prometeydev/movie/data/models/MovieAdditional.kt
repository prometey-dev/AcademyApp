package ru.prometeydev.movie.data.models

import androidx.annotation.DrawableRes

@Deprecated("not use")
data class MovieAdditional(
    @DrawableRes
    val pictureDrawable: Int,
    val storyLine: String,
    val actors: ArrayList<Actor>
)
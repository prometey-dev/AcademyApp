package ru.prometeydev.movie.data.models

import androidx.annotation.DrawableRes

@Deprecated("not use")
data class Actor(
    val id: Long,
    val name: String?,
    @DrawableRes
    val avatarDrawable: Int
)
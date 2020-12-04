package ru.prometeydev.movie.data.models

import androidx.annotation.DrawableRes


data class Actor(
    val name: String,
    @DrawableRes
    val avatarDrawable: Int
)
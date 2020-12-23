package ru.prometeydev.movie.domain

import kotlinx.serialization.Serializable

@Serializable
class JsonGenre(
    val id: Int,
    val name: String
)
package ru.prometeydev.movie.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class JsonActor(
    val id: Int,
    val name: String,
    @SerialName("profile_path")
    val profilePicture: String
)
package ru.prometeydev.movie.model.domain

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ActorDto(
    val id: Int,
    val name: String,
    @Json(name = "profile_path")
    val profilePicture: String?
)
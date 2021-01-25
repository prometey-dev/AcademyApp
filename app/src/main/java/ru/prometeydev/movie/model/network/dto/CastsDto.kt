package ru.prometeydev.movie.model.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CastsDto(
    val id: Int,
    @Json(name = "cast")
    val actors: List<ActorDto>
)
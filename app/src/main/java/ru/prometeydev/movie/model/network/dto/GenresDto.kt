package ru.prometeydev.movie.model.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GenresDto(
    @Json(name = "genres") val genres: List<GenreDto>
)
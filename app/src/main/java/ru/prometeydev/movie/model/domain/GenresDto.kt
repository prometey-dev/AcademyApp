package ru.prometeydev.movie.model.domain

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GenresDto(
    @Json(name = "genres") val genres: List<GenreDto>
)
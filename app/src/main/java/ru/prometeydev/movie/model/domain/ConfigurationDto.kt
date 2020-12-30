package ru.prometeydev.movie.model.domain

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ConfigurationDto(
    @Json(name = "images") val images: ImagesDto
)
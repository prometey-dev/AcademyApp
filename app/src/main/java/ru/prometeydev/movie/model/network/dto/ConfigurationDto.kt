package ru.prometeydev.movie.model.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ConfigurationDto(
    val images: ImagesDto
)
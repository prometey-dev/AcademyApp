package ru.prometeydev.movie.model.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideoResultDto(
    val id: String,
    val iso_639_1: String,
    val iso_3166_1: String,
    val key: String,
    val name: String,
    val site: String,
    val size: Int,
    val type: String
)
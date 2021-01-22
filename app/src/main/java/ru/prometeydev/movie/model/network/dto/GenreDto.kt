package ru.prometeydev.movie.model.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenreDto(
    val id: Int,
    val name: String
)
package ru.prometeydev.movie.model.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    @Json(name = "status_message")
    val message: String,
    @Json(name = "status_code")
    val code: String
)
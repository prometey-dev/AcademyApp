package ru.prometeydev.movie.model.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class VideosMovieResponse(
    val id: Int,
    val results: List<VideoResultDto>
)
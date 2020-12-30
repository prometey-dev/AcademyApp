package ru.prometeydev.movie.model.domain

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PopularMoviesDto(
    @Json(name = "page") val page: Int,
    @Json(name = "results") val results: List<MovieDto>,
    @Json(name = "total_results") val totalCount: Int,
    @Json(name = "total_pages") val pagesCount: Int
)
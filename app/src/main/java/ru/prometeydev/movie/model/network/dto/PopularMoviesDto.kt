package ru.prometeydev.movie.model.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PopularMoviesDto(
    val page: Int,
    val results: List<MovieDto>,
    @Json(name = "total_results")
    val totalCount: Int,
    @Json(name = "total_pages")
    val pagesCount: Int
)
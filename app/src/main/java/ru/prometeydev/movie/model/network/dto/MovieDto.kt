package ru.prometeydev.movie.model.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieDto(
    val id: Int,
    val title: String,
    @Json(name = "poster_path")
    val posterPicture: String?,
    @Json(name = "backdrop_path")
    val backdropPicture: String?,
    @Json(name = "genre_ids")
    val genreIds: List<Int>,
    @Json(name = "vote_count")
    val votesCount: Int,
    @Json(name = "vote_average")
    val ratings: Float,
    val overview: String,
    val adult: Boolean
)
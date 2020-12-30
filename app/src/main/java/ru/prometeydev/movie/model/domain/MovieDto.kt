package ru.prometeydev.movie.model.domain

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieDto(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "poster_path") val posterPicture: String?,
    @Json(name = "backdrop_path") val backdropPicture: String?,
    @Json(name = "genre_ids") val genreIds: List<Int>,
    @Json(name = "vote_count") val votesCount: Int,
    @Json(name = "vote_average") val ratings: Float,
    @Json(name = "overview") val overview: String,
    @Json(name = "adult") val adult: Boolean
)
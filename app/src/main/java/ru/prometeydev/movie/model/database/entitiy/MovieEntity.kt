package ru.prometeydev.movie.model.database.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.prometeydev.movie.model.network.dto.GenreDto
import ru.prometeydev.movie.model.network.dto.ActorDto

@Entity(tableName = "movies")
data class MovieEntity(
        @PrimaryKey(autoGenerate = true)
        val pk: Int = 0,
        val movieId: Int,
        val title: String,
        val overview: String?,
        val posterPath: String?,
        val backdropPath: String?,
        val numberOfRatings: Int,
        val minimumAge: Int,
        val ratings: Float,
        val adult: Boolean,
        val runtime: Int?,
        val genres: List<GenreEntity>,
        val actors: List<ActorEntity>,
        val video: String
)
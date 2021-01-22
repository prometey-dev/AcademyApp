package ru.prometeydev.movie.model.database.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.prometeydev.movie.model.network.dto.GenreDto
import ru.prometeydev.movie.model.database.entitiy.MoviesEntity.Companion.tableName
import ru.prometeydev.movie.model.network.dto.ActorDto

@Entity(tableName = tableName)
data class MoviesEntity(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val title: String,
        val overview: String?,
        val posterPath: String?,
        val backdropPath: String?,
        val numberOfRatings: Int,
        val minimumAge: Int,
        val ratings: Float,
        val adult: Boolean,
        val runtime: Int?,
        val genres: List<GenreDto>,
        val actors: List<ActorDto>
) {

        companion object {
                const val tableName = "movies"
        }

}
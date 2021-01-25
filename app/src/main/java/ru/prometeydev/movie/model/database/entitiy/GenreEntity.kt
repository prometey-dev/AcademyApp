package ru.prometeydev.movie.model.database.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "genres")
data class GenreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String
)
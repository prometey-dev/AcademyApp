package ru.prometeydev.movie.model.database.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actors")
data class ActorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val picturePath: String?
)
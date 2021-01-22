package ru.prometeydev.movie.model.database.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.prometeydev.movie.model.database.entitiy.ActorEntity.Companion.tableName

@Entity(tableName = tableName)
data class ActorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val picturePath: String?
) {

    companion object {
        const val tableName = "actors"
    }

}
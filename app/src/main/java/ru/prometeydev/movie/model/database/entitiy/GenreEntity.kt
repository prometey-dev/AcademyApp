package ru.prometeydev.movie.model.database.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.prometeydev.movie.model.database.entitiy.GenreEntity.Companion.tableName

@Entity(tableName = tableName)
data class GenreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String
) {

    companion object {
        const val tableName = "genres"
    }

}
package ru.prometeydev.movie.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.prometeydev.movie.model.database.converters.DataConverter
import ru.prometeydev.movie.model.database.dao.MoviesDao
import ru.prometeydev.movie.model.database.dao.MoviesRemoteKeysDao
import ru.prometeydev.movie.model.database.entitiy.ActorEntity
import ru.prometeydev.movie.model.database.entitiy.GenreEntity
import ru.prometeydev.movie.model.database.entitiy.MovieEntity
import ru.prometeydev.movie.model.database.entitiy.MoviesRemoteKeysEntity

@Database(
    entities = [
        ActorEntity::class,
        GenreEntity::class,
        MovieEntity::class,
        MoviesRemoteKeysEntity::class
    ],
    version = 10,
    exportSchema = false
)
@TypeConverters(DataConverter::class)
abstract class MoviesDatabase : RoomDatabase() {

    abstract fun moviesDao(): MoviesDao
    abstract fun keysDao(): MoviesRemoteKeysDao

    companion object {

        private const val databaseName = "movies-db"

        private var instance: MoviesDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                MoviesDatabase::class.java,
                databaseName
            ).fallbackToDestructiveMigration()
             .build()

    }

}
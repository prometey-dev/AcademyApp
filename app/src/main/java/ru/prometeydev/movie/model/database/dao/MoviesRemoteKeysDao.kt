package ru.prometeydev.movie.model.database.dao

import androidx.room.*
import ru.prometeydev.movie.model.database.entitiy.MoviesRemoteKeysEntity

@Dao
interface MoviesRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<MoviesRemoteKeysEntity>)

    @Query("SELECT * FROM remote_keys WHERE movieId = :movieId")
    suspend fun remoteKeysByMovieId(movieId: Int): MoviesRemoteKeysEntity?

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()
}
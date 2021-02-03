package ru.prometeydev.movie.model.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import ru.prometeydev.movie.model.database.entitiy.ActorEntity
import ru.prometeydev.movie.model.database.entitiy.GenreEntity
import ru.prometeydev.movie.model.database.entitiy.MovieEntity

@Dao
interface MoviesDao {

    @Query("SELECT * FROM movies")
    fun getAllMovies(): PagingSource<Int, MovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMovies(movies: List<MovieEntity>)

    @Query("DELETE FROM movies")
    suspend fun clearAllMovies(): Int

    @Query("UPDATE movies SET actors = :actors WHERE movieId = :movieId")
    suspend fun addActorsToTheMovie(movieId: Int, actors: List<ActorEntity>): Int

    @Query("SELECT * FROM movies WHERE movieId = :movieId")
    suspend fun getMovieById(movieId: Int): MovieEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateGenres(genres: List<GenreEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateActors(actors: List<ActorEntity>)

    @Query("UPDATE movies SET numberOfRatings = :numberOfRatings, ratings = :ratings WHERE movieId = :movieId")
    suspend fun updateRating(movieId: Int, numberOfRatings: Int, ratings: Float): Int

}
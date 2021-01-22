package ru.prometeydev.movie.model.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.prometeydev.movie.model.database.entitiy.ActorEntity
import ru.prometeydev.movie.model.database.entitiy.GenreEntity
import ru.prometeydev.movie.model.database.entitiy.MoviesEntity
import ru.prometeydev.movie.model.network.dto.ActorDto

@Dao
interface MoviesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateMovies(movies: List<MoviesEntity>)

    @Query("UPDATE movies SET actors = :actors WHERE id = :movieId")
    fun addActorsToTheMovie(movieId: Int, actors: List<ActorDto>): Int

    @Query("SELECT * FROM movies WHERE id = :movieId")
    fun getMovieById(movieId: Int): Flow<MoviesEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateGenres(genres: List<GenreEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateActors(actors: List<ActorEntity>)

}
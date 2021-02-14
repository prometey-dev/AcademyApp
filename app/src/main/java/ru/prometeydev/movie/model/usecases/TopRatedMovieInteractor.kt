package ru.prometeydev.movie.model.usecases

import ru.prometeydev.movie.model.domain.Movie
import ru.prometeydev.movie.model.mappers.mapMoviesEntityToDomain
import ru.prometeydev.movie.model.usecases.base.BaseUseCases

class TopRatedMovieInteractor : BaseUseCases() {

    private var topRatedMovie: Movie? = null

    suspend fun getNewTopRatedMovie(): Movie? {
        val movies = repo.db.moviesDao().getAllMovies()
        return movies.maxByOrNull { it.ratings }?.let { movie ->
            if (
                movie.ratings > topRatedMovie?.ratings ?: 0f
                && topRatedMovie?.id != movie.movieId
            ) {
                topRatedMovie = mapMoviesEntityToDomain(movie)
                topRatedMovie
            } else {
                null
            }
        }
    }

}
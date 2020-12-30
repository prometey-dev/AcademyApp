package ru.prometeydev.movie.model

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.prometeydev.movie.common.errors.ConnectionErrorException
import ru.prometeydev.movie.common.errors.UnexpectedErrorException
import ru.prometeydev.movie.model.domain.MovieDetailsDto
import ru.prometeydev.movie.model.domain.MovieDto
import ru.prometeydev.movie.model.network.MoviesApi
import ru.prometeydev.movie.model.network.MoviesApiProvider
import java.io.IOException
import java.lang.Exception

class MoviesRepository {

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    private val api: MoviesApi = MoviesApiProvider().api

    private lateinit var baseImageUrl: String

    suspend fun loadMovies(): List<Movie> = withContext(dispatcher) {
        try {
            baseImageUrl = getImageUrl()
            val genresMap = loadGenres()

            val data = api.getMoviesPopular()
            mapMovies(data.results, genresMap)
        } catch (ex: Exception) {
            throw handleException(ex)
        }
    }

    suspend fun getMovieById(movieId: Int): MovieDetails = withContext(dispatcher) {
        try {
            baseImageUrl = getImageUrl()
            val genresMap = loadGenres()
            val actorsMap = loadActorsByMovie(movieId)

            val data = api.getMovieDetails(movieId)
            mapMovieDetails(data, genresMap, actorsMap)
        } catch (ex: Exception) {
            throw handleException(ex)
        }
    }

    private fun handleException(ex: Exception): Exception {
        return when (ex) {
            is IOException, is HttpException -> ConnectionErrorException()
            else -> UnexpectedErrorException()
        }
    }

    private suspend fun getImageUrl(): String = withContext(dispatcher) {
        api.getConfiguration().images.secureBaseUrl
    }

    private suspend fun loadGenres(): List<Genre> = withContext(dispatcher) {
        val data = api.getGenresList()
        data.genres.map {
            Genre(
                id = it.id,
                name = it.name
            )
        }
    }

    private suspend fun loadActorsByMovie(movieId: Int): List<Actor> = withContext(dispatcher) {
        val data = api.getCredits(movieId)
        data.actors.map {
            Actor(
                id = it.id,
                name = it.name,
                picture = fullImageUrl(it.profilePicture)
            )
        }
    }

    private fun fullImageUrl(relativeUrl: String?): String {
        return relativeUrl?.let {
            "$baseImageUrl$IMAGE_SIZE$relativeUrl"
        } ?: ""
    }

    private fun mapMovies(
        movies: List<MovieDto>,
        genres: List<Genre>
    ): List<Movie> {
        val genresMap = genres.associateBy { it.id }

        return movies.map { movie ->
            Movie(
                id = movie.id,
                title = movie.title,
                overview = movie.overview,
                poster = fullImageUrl(movie.posterPicture),
                backdrop = fullImageUrl(movie.backdropPicture),
                numberOfRatings = movie.votesCount,
                minimumAge = if (movie.adult) 16 else 13,
                ratings = movie.ratings,
                adult = movie.adult,
                genres = movie.genreIds.map {
                    genresMap[it] ?: throw IllegalArgumentException("Genre not found")
                }
            )
        }
    }

    private fun mapMovieDetails(
        movie: MovieDetailsDto,
        genres: List<Genre>,
        actors: List<Actor>
    ): MovieDetails {
        return MovieDetails(
            id = movie.id,
            title = movie.title,
            overview = movie.overview,
            poster = fullImageUrl(movie.posterPicture),
            backdrop = fullImageUrl(movie.backdropPicture),
            numberOfRatings = movie.votesCount,
            minimumAge = if (movie.adult) 16 else 13,
            ratings = movie.ratings,
            adult = movie.adult,
            runtime = movie.runtime,
            genres = genres,
            actors = actors
        )
    }

    companion object {
        private const val IMAGE_SIZE = "w342"
    }

}
package ru.prometeydev.movie.model

import ru.prometeydev.movie.model.domain.MovieDetailsDto
import ru.prometeydev.movie.model.domain.MovieDto
import ru.prometeydev.movie.model.domain.PopularMoviesDto
import ru.prometeydev.movie.model.local.Actor
import ru.prometeydev.movie.model.local.Genre
import ru.prometeydev.movie.model.local.Movie
import ru.prometeydev.movie.model.local.MovieDetails
import ru.prometeydev.movie.model.network.MoviesApi
import ru.prometeydev.movie.model.network.MoviesApiProvider

class MoviesRepository(network: MoviesApiProvider): BaseUseCases() {

    private val api: MoviesApi = network.api

    private lateinit var baseImageUrl: String

    private var genres: List<Genre> = emptyList()

    suspend fun loadMovies(page: Int): PopularMoviesDto = execute {
        baseImageUrl = getImageUrl()

        if (genres.isEmpty()) {
            genres = loadGenres()
        }
        api.getMoviesPopular(page)
        //mapMovies(data.results)
    }

    suspend fun getMovieById(movieId: Int): MovieDetails = execute {
        baseImageUrl = getImageUrl()

        val actorsMap = loadActorsByMovie(movieId)
        val data = api.getMovieDetails(movieId)
        mapMovieDetails(data, actorsMap)
    }

    private suspend fun getImageUrl(): String = execute {
        api.getConfiguration().images.secureBaseUrl
    }

    private suspend fun loadGenres(): List<Genre> = execute {
        val data = api.getGenresList()
        data.genres.map {
            Genre(
                id = it.id,
                name = it.name
            )
        }
    }

    private suspend fun loadActorsByMovie(movieId: Int): List<Actor> = execute {
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

    fun mapMovies(
        movies: List<MovieDto>
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
            genres = movie.genres.map { Genre(it.id, it.name) },
            actors = actors
        )
    }

    companion object {
        private const val IMAGE_SIZE = "w342"
    }

}
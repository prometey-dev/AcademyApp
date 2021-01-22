package ru.prometeydev.movie.model

import kotlinx.coroutines.flow.*
import ru.prometeydev.movie.model.network.dto.*
import ru.prometeydev.movie.model.domain.Actor
import ru.prometeydev.movie.model.domain.Genre
import ru.prometeydev.movie.model.domain.Movie
import ru.prometeydev.movie.model.network.MoviesApi
import ru.prometeydev.movie.model.database.dao.MoviesDao
import ru.prometeydev.movie.model.database.entitiy.ActorEntity
import ru.prometeydev.movie.model.database.entitiy.GenreEntity
import ru.prometeydev.movie.model.database.entitiy.MoviesEntity
import ru.prometeydev.movie.ui.base.Result

class MoviesRepository(
    private val api: MoviesApi,
    private val dao: MoviesDao
): BaseUseCases() {

    private var baseImageUrl = ""

    private var genres: List<GenreDto> = emptyList()

    fun getMovieById(movieId: Int): Flow<Result<Movie>> {
        return networkBoundResult(
                fetchFromLocal = { dao.getMovieById(movieId) },
                mapEntityToModel = { entity ->
                    mapMoviesEntityToDomain(entity)
                },
                shouldFetchFromRemote = { entity ->
                    entity?.actors.isNullOrEmpty()
                },
                fetchFromRemote = { api.getCredits(movieId) },
                saveRemoteData = { casts ->
                    dao.addActorsToTheMovie(movieId, casts.actors)
                    dao.insertOrUpdateActors(
                        actors = mapListActorsDtoToEntity(casts.actors)
                    )
                }
        )
    }

    suspend fun loadMovies(page: Int): PopularMoviesDto = execute {
        if (baseImageUrl.isEmpty()) {
            baseImageUrl = getImageUrl()
        }

        if (genres.isEmpty()) {
            genres = loadGenres()
            dao.insertOrUpdateGenres(
                genres = mapListGenresDtoToEntity(genres)
            )
        }
        val response = api.getMoviesPopular(page)
        dao.insertOrUpdateMovies(
            movies = mapListMoviesDtoToEntity(response.results)
        )
        response
    }

    private suspend fun getImageUrl(): String {
        return api.getConfiguration().images.secureBaseUrl
    }

    private suspend fun loadGenres(): List<GenreDto> {
        val data = api.getGenresList()
        return data.genres.map {
            GenreDto(
                id = it.id,
                name = it.name
            )
        }
    }

    private fun fullImageUrl(relativeUrl: String?): String {
        return relativeUrl?.let {
            "$baseImageUrl$IMAGE_SIZE$relativeUrl"
        } ?: ""
    }

    fun mapListMoviesDtoToDomain(movies: List<MovieDto>): List<Movie> {
        val genresMap = genres.map { Genre(id = it.id, name = it.name) }.associateBy { it.id }

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

    private fun mapMoviesEntityToDomain(movie: MoviesEntity): Movie {
        return Movie(
                id = movie.id,
                title = movie.title,
                overview = movie.overview,
                poster = movie.posterPath,
                backdrop = movie.backdropPath,
                numberOfRatings = movie.numberOfRatings,
                minimumAge = movie.minimumAge,
                ratings = movie.ratings,
                adult = movie.adult,
                runtime = movie.runtime,
                genres = movie.genres.map { Genre(it.id, it.name) },
                actors = movie.actors.map { Actor(it.id, it.name, fullImageUrl(it.profilePicture)) }
        )
    }

    private fun mapMovieDtoToEntity(movie: MovieDto): MoviesEntity {
        val genresMap = genres.associateBy { it.id }

        return MoviesEntity(
            id = movie.id,
            title = movie.title,
            overview = movie.overview,
            posterPath = fullImageUrl(movie.posterPicture),
            backdropPath = fullImageUrl(movie.backdropPicture),
            numberOfRatings = movie.votesCount,
            minimumAge = if (movie.adult) 16 else 13,
            ratings = movie.ratings,
            adult = movie.adult,
            runtime = null,
            genres = movie.genreIds.map {
                genresMap[it] ?: throw IllegalArgumentException("Genre not found")
            },
            actors = emptyList()
        )
    }

    private fun mapListMoviesDtoToEntity(movies: List<MovieDto>): List<MoviesEntity> {
        return movies.map { mapMovieDtoToEntity(it) }
    }

    private fun mapListGenresDtoToEntity(genres: List<GenreDto>): List<GenreEntity> {
        return genres.map { genre ->
            GenreEntity(id = genre.id, name = genre.name)
        }
    }

    private fun mapListActorsDtoToEntity(actors: List<ActorDto>): List<ActorEntity> {
        return actors.map { actor ->
            ActorEntity(
                id = actor.id,
                name = actor.name,
                picturePath = fullImageUrl(actor.profilePicture)
            )
        }
    }

    companion object {
        private const val IMAGE_SIZE = "w342"
    }

}
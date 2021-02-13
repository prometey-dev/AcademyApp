package ru.prometeydev.movie.model.mappers

import ru.prometeydev.movie.model.database.entitiy.ActorEntity
import ru.prometeydev.movie.model.database.entitiy.GenreEntity
import ru.prometeydev.movie.model.database.entitiy.MovieEntity
import ru.prometeydev.movie.model.domain.Actor
import ru.prometeydev.movie.model.domain.Genre
import ru.prometeydev.movie.model.domain.Movie
import ru.prometeydev.movie.model.network.dto.ActorDto
import ru.prometeydev.movie.model.network.dto.GenreDto
import ru.prometeydev.movie.model.network.dto.MovieDto

private const val IMAGE_SIZE = "w342"

private fun String.fullImageUrl(relativeUrl: String?): String {
    return relativeUrl?.let {
        "$this${IMAGE_SIZE}$relativeUrl"
    } ?: ""
}

fun mapMoviesEntityToDomain(movie: MovieEntity): Movie {
    return Movie(
            id = movie.movieId,
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
            actors = movie.actors.map { Actor(it.id, it.name, it.picturePath) }
    )
}

fun mapMovieDtoToEntity(movie: MovieDto, genres: List<Genre>, baseImageUrl: String): MovieEntity {
    val genresMap = genres
            .map { GenreEntity(it.id, it.name) }
            .associateBy { it.id }

    return MovieEntity(
            movieId = movie.id,
            title = movie.title,
            overview = movie.overview,
            posterPath = baseImageUrl.fullImageUrl(movie.posterPicture),
            backdropPath = baseImageUrl.fullImageUrl(movie.backdropPicture),
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

fun mapListMoviesDtoToEntity(movies: List<MovieDto>, genres: List<Genre>, baseImageUrl: String): List<MovieEntity> {
    return movies.map { mapMovieDtoToEntity(it, genres, baseImageUrl) }
}

fun mapListGenresDtoToEntity(genres: List<GenreDto>): List<GenreEntity> {
    return genres.map { genre ->
        GenreEntity(id = genre.id, name = genre.name)
    }
}

fun mapListGenresEntityToDomain(genres: List<GenreEntity>): List<Genre> {
    return genres.map { genre ->
        Genre(id = genre.id, name = genre.name)
    }
}

fun mapListActorsDtoToEntity(actors: List<ActorDto>, baseImageUrl: String): List<ActorEntity> {
    return actors.map { actor ->
        ActorEntity(
                id = actor.id,
                name = actor.name,
                picturePath = baseImageUrl.fullImageUrl(actor.profilePicture)
        )
    }
}
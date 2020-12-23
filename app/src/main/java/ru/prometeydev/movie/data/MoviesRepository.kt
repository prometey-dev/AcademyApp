package ru.prometeydev.movie.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import ru.prometeydev.movie.domain.JsonActor
import ru.prometeydev.movie.domain.JsonGenre
import ru.prometeydev.movie.domain.JsonMovie

class MoviesRepository(private val dispatcher: CoroutineDispatcher) {

    private val jsonFormat = Json { ignoreUnknownKeys = true }

    suspend fun loadMovies(): List<Movie> = withContext(dispatcher) {
        val genresMap = loadGenres()
        val actorsMap = loadActors()

        val data = readAssetFileToString("data.json")
        parseMovies(data, genresMap, actorsMap)
    }

    private suspend fun loadGenres(): List<Genre> = withContext(dispatcher) {
        val data = readAssetFileToString("genres.json")
        parseGenres(data)
    }

    private suspend fun loadActors(): List<Actor> = withContext(dispatcher) {
        val data = readAssetFileToString("people.json")
        parseActors(data)
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun readAssetFileToString(fileName: String): String {
        val stream = javaClass.getResourceAsStream("/assets/$fileName")
        return stream.bufferedReader().readText()
    }

    private fun parseGenres(data: String): List<Genre> {
        val jsonGenres = jsonFormat.decodeFromString<List<JsonGenre>>(data)
        return jsonGenres.map { Genre(id = it.id, name = it.name) }
    }

    private fun parseActors(data: String): List<Actor> {
        val jsonActors = jsonFormat.decodeFromString<List<JsonActor>>(data)
        return jsonActors.map { Actor(id = it.id, name = it.name, picture = it.profilePicture) }
    }

    private fun parseMovies(
        data: String,
        genres: List<Genre>,
        actors: List<Actor>
    ): List<Movie> {
        val genresMap = genres.associateBy { it.id }
        val actorsMap = actors.associateBy { it.id }

        val jsonMovies = jsonFormat.decodeFromString<List<JsonMovie>>(data)

        return jsonMovies.map { jsonMovie ->
            Movie(
                id = jsonMovie.id,
                title = jsonMovie.title,
                overview = jsonMovie.overview,
                poster = jsonMovie.posterPicture,
                backdrop = jsonMovie.backdropPicture,
                numberOfRatings = jsonMovie.votesCount,
                minimumAge = if (jsonMovie.adult) 16 else 13,
                ratings = jsonMovie.ratings,
                adult = jsonMovie.adult,
                runtime = jsonMovie.runtime,
                genres = jsonMovie.genreIds.map {
                    genresMap[it] ?: throw IllegalArgumentException("Genre not found")
                },
                actors = jsonMovie.actors.map {
                    actorsMap[it] ?: throw IllegalArgumentException("Actor not found")
                }
            )
        }
    }

}
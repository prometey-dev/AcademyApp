package ru.prometeydev.movie.data.domain

import ru.prometeydev.movie.R
import ru.prometeydev.movie.data.models.Movie

class MoviesDataSource {
    fun getMovies(): List<Movie> = listOf(
        Movie(
            name = "Avengers: End Game",
            genre = "Action, Adventure, Drama",
            duration = 137,
            rating = 4f,
            reviewsCount = 125,
            ageLimit = 13,
            filmCoverDrawable = R.drawable.avengers,
            hasLike = false
        ),
        Movie(
            name = "Tenet",
            genre = "Action, Sci-Fi, Thriller",
            duration = 97,
            rating = 5f,
            reviewsCount = 98,
            ageLimit = 16,
            filmCoverDrawable = R.drawable.tenet,
            hasLike = true
        ),
        Movie(
            name = "Black Widow",
            genre = "Action, Adventure, Sci-Fi",
            duration = 102,
            rating = 4f,
            reviewsCount = 38,
            ageLimit = 13,
            filmCoverDrawable = R.drawable.black_widow,
            hasLike = false
        ),
        Movie(
            name = "Wonder Woman 1984",
            genre = "Action, Adventure, Fantasy",
            duration = 120,
            rating = 5f,
            reviewsCount = 74,
            ageLimit = 13,
            filmCoverDrawable = R.drawable.wonder_woman,
            hasLike = false
        )
    )
}
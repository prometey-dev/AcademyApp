package ru.prometeydev.movie.data.domain

import ru.prometeydev.movie.R
import ru.prometeydev.movie.data.models.Movie
import ru.prometeydev.movie.data.models.MovieAdditional

@Deprecated("not use")
class MoviesDataSource {
    fun getMovies(): List<Movie> = listOf(
        Movie(
            id = 1,
            name = "Avengers: End Game",
            genre = "Action, Adventure, Drama",
            duration = 137,
            rating = 4f,
            reviewsCount = 125,
            ageLimit = 13,
            filmCoverDrawable = R.drawable.avengers,
            hasLike = false,
            additional = MovieAdditional(
                pictureDrawable = R.drawable.movie_logo_detail,
                storyLine = "After the devastating events of Avengers: Infinity War, the universe is "
                        + "in ruins. With the help of remaining allies, the Avengers assemble once "
                        + "more in order to reverse Thanos\\' actions and restore balance to the universe.",
                actors = ActorsDataSource().getActors()
            )
        ),
        Movie(
            id = 2,
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
            id = 3,
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
            id = 4,
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
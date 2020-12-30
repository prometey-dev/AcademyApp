package ru.prometeydev.movie.model.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.prometeydev.movie.model.domain.*

interface MoviesApi {

    @GET("movie/popular")
    suspend fun getMoviesPopular(): PopularMoviesDto

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") movieId: Int): MovieDetailsDto

    @GET("movie/{movie_id}/credits")
    suspend fun getCredits(@Path("movie_id") movieId: Int): CastsDto

    @GET("genre/movie/list")
    suspend fun getGenresList(): GenresDto

    @GET("configuration")
    suspend fun getConfiguration(): ConfigurationDto

}
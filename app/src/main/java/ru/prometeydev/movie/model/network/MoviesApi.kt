package ru.prometeydev.movie.model.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.prometeydev.movie.model.domain.*

interface MoviesApi {

    @GET("movie/popular?api_key=$API_KEY")
    suspend fun getMoviesPopular(): PopularMoviesDto

    @GET("movie/{movie_id}?api_key=$API_KEY")
    suspend fun getMovieDetails(@Path("movie_id") movieId: Int): MovieDetailsDto

    @GET("movie/{movie_id}/credits?api_key=$API_KEY")
    suspend fun getCredits(@Path("movie_id") movieId: Int): CastsDto

    @GET("genre/movie/list?api_key=$API_KEY")
    suspend fun getGenresList(): GenresDto

    @GET("configuration?api_key=$API_KEY")
    suspend fun getConfiguration(): ConfigurationDto

    companion object {
        private const val API_KEY = "6919274aeda3b6ad96593bb0eafb4afd"
    }

}
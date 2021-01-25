package ru.prometeydev.movie.model.network

import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.prometeydev.movie.model.network.dto.*
import ru.prometeydev.movie.model.network.response.ApiResponse

interface MoviesApi {

    @GET("movie/{movie_id}/credits")
    suspend fun getCredits(@Path("movie_id") movieId: Int): CastsDto

    @GET("movie/popular")
    suspend fun getMoviesPopular(@Query("page") page: Int): PopularMoviesDto

    @GET("genre/movie/list")
    suspend fun getGenresList(): GenresDto

    @GET("configuration")
    suspend fun getConfiguration(): ConfigurationDto

}
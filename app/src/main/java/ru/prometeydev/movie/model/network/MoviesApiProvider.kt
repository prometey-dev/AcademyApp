package ru.prometeydev.movie.model.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

class MoviesApiProvider {

    lateinit var api: MoviesApi

    private val okHttpClient: OkHttpClient

    init {
        val loggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        okHttpClient = OkHttpClient.Builder()
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()

        setupApi()
    }

    private fun setupApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()

        api = retrofit.create()
    }

    companion object {
        private const val CONNECTION_TIMEOUT = 15L
        private const val READ_TIMEOUT = 15L

        private const val BASE_URL = "https://api.themoviedb.org/3/"
    }

}
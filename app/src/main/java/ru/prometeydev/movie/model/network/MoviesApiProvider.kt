package ru.prometeydev.movie.model.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import ru.prometeydev.movie.BuildConfig
import java.util.concurrent.TimeUnit

class MoviesApiProvider {

    lateinit var api: MoviesApi

    private val okHttpClient: OkHttpClient

    init {
        val tokenInterceptor = TokenInterceptor()
        val loggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        okHttpClient = OkHttpClient.Builder()
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(tokenInterceptor)
            .build()

        setupApi()
    }

    private fun setupApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()

        api = retrofit.create()
    }

    private class TokenInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val urlWithToken = originalRequest.url.newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .build()

            val request = originalRequest.newBuilder()
                .url(urlWithToken)
                .build()

            return chain.proceed(request)
        }

    }

    companion object {
        private const val CONNECTION_TIMEOUT = 15L
        private const val READ_TIMEOUT = 15L

        private const val API_KEY = "6919274aeda3b6ad96593bb0eafb4afd"
    }

}
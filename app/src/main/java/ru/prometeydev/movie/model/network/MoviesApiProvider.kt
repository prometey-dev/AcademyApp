package ru.prometeydev.movie.model.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import ru.prometeydev.movie.BuildConfig
import ru.prometeydev.movie.model.network.calladapter.FlowCallAdapterFactory
import java.util.concurrent.TimeUnit

class MoviesApiProvider {

    lateinit var api: MoviesApi

    private val tokenInterceptor = TokenInterceptor()
    private val loggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

    private val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(tokenInterceptor)
            .build()

    init {
        setupApi()
    }

    private fun setupApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addCallAdapterFactory(FlowCallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()

        api = retrofit.create()
    }

    private class TokenInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val urlWithToken = originalRequest.url.newBuilder()
                .addQueryParameter("api_key", BuildConfig.API_KEY)
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
    }

}
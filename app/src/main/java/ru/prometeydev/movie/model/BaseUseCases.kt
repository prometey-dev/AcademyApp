package ru.prometeydev.movie.model

import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.prometeydev.movie.common.errors.ConnectionErrorException
import ru.prometeydev.movie.common.errors.UnexpectedErrorException
import ru.prometeydev.movie.model.domain.ErrorResponse
import java.io.IOException
import java.lang.Exception
import java.net.ConnectException
import java.net.ProtocolException
import java.net.SocketException
import java.net.SocketTimeoutException

abstract class BaseUseCases {

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    private val moshi: Moshi = Moshi.Builder().build()

    protected suspend fun <T> execute(request: suspend () -> T) = withContext(dispatcher) {
        try {
            request.invoke()
        } catch (ex: Exception) {
            throw handleException(ex)
        }
    }

    private fun handleException(error: Throwable): Throwable {
        return when (error) {
            is IOException -> ConnectionErrorException()
            is HttpException -> handleHttpException(error)
            else -> UnexpectedErrorException()
        }
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun handleHttpException(httpException: HttpException): Throwable {
        val errorMessage = httpException.response()?.errorBody()?.string()
        val jsonAdapter = moshi.adapter(ErrorResponse::class.java)
        val errorResponse = jsonAdapter.fromJson(errorMessage)
        return Throwable(errorResponse?.message)
    }

}
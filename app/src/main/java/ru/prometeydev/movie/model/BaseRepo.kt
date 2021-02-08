package ru.prometeydev.movie.model

import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.prometeydev.movie.common.errors.ConnectionErrorException
import ru.prometeydev.movie.common.errors.ErrorResponseException
import ru.prometeydev.movie.common.errors.UnexpectedErrorException
import ru.prometeydev.movie.model.network.dto.ErrorResponse
import ru.prometeydev.movie.model.network.response.ApiErrorResponse
import ru.prometeydev.movie.model.network.response.ApiResponse
import ru.prometeydev.movie.model.network.response.ApiSuccessResponse
import ru.prometeydev.movie.ui.base.Result
import java.io.IOException
import java.lang.Exception

abstract class BaseRepo {

    protected val dispatcher: CoroutineDispatcher = Dispatchers.IO

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
        return errorResponse?.let { ErrorResponseException(it) } ?: UnexpectedErrorException()
    }

}
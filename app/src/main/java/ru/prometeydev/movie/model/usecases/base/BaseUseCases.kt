package ru.prometeydev.movie.model.usecases.base

import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.HttpException
import ru.prometeydev.movie.common.errors.ConnectionErrorException
import ru.prometeydev.movie.common.errors.ErrorResponseException
import ru.prometeydev.movie.common.errors.UnexpectedErrorException
import ru.prometeydev.movie.model.Repo
import ru.prometeydev.movie.model.network.dto.ErrorResponse
import java.io.IOException
import java.lang.Exception

abstract class BaseUseCases() : KoinComponent {

    protected val repo: Repo by inject()

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
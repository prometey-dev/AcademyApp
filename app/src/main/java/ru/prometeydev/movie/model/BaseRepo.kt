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

    fun <MODEL, ENTITY, REMOTE> networkBoundResult(
            fetchFromLocal: () -> Flow<ENTITY>,
            mapEntityToModel: (ENTITY) -> MODEL,
            shouldFetchFromRemote: (ENTITY?) -> Boolean = { true },
            fetchFromRemote: () -> Flow<ApiResponse<REMOTE>>,
            saveRemoteData: (REMOTE) -> Unit = { },
    ) = flow {
        emit(Result.Loading)

        val localData = fetchFromLocal().first()

        if (shouldFetchFromRemote(localData)) {
            emit(Result.Loading)

            fetchFromRemote().collect { apiResponse ->
                when (apiResponse) {
                    is ApiSuccessResponse -> {
                        apiResponse.body?.let { saveRemoteData(it) }
                    }

                    is ApiErrorResponse -> {
                        //emit(Result.Error(apiResponse.errorMessage))
                    }
                }
            }
        }

        emitAll(
            fetchFromLocal()
                .map { mapEntityToModel(it) }
                .map { Result.Success(it) }
        )
    }.flowOn(dispatcher)

    protected suspend fun <T> execute(request: suspend () -> T) = withContext(dispatcher) {
        try {
            request.invoke()
        } catch (ex: Exception) {
            //throw handleException(ex)
        }
    }

    //todo Доработать данный базовый метод для вызова из репозитория
    protected fun <T> flowExecute(request: () -> T, errorRequest: () -> T) = flow {
        emit(Result.Loading)
        val response = request.invoke()
        emit(response)
    }.catch { e ->
        emit(Result.Error(handleException(e.cause)))
        emit(Result.Success(errorRequest.invoke()))
    }.flowOn(dispatcher)

    private fun handleException(error: Throwable?): Throwable {
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
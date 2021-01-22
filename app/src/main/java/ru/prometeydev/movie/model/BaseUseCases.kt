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

abstract class BaseUseCases {

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    private val moshi: Moshi = Moshi.Builder().build()

    fun <MODEL, ENTITY, REMOTE> networkBoundResult(
            fetchFromLocal: () -> Flow<ENTITY>,
            mapEntityToModel: (ENTITY) -> MODEL,
            shouldFetchFromRemote: (ENTITY?) -> Boolean = { true },
            fetchFromRemote: () -> Flow<ApiResponse<REMOTE>>,
            saveRemoteData: (REMOTE) -> Unit = { },
            onFetchFailed: (errorBody: String?, statusCode: Int) -> Unit = { _: String?, _: Int -> }
    ) = flow {
        emit(Result.Loading)

        val localFlow = fetchFromLocal()
        val localData = localFlow.first()

        if (shouldFetchFromRemote(localData)) {
            emit(Result.Loading)

            fetchFromRemote().collect { apiResponse ->
                when (apiResponse) {
                    is ApiSuccessResponse -> {
                        apiResponse.body?.let { saveRemoteData(it) }
                        emitAll(
                            fetchFromLocal().map { dbData ->
                                mapEntityToModel(dbData)
                            }.map { modelData ->
                                Result.Success(modelData)
                            }
                        )
                    }

                    is ApiErrorResponse -> {
                        onFetchFailed(apiResponse.errorMessage, apiResponse.statusCode)
                        emit(Result.Error(apiResponse.errorMessage))
                        emitAll(
                            localFlow
                                .map { mapEntityToModel(it) }
                                .map { Result.Success(it) }
                        )
                    }
                }
            }

        } else {
            emitAll(
                fetchFromLocal()
                    .map { mapEntityToModel(it) }
                    .map { Result.Success(it) }
            )
        }
    }.flowOn(dispatcher)

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
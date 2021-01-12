package ru.prometeydev.movie.common.errors

import ru.prometeydev.movie.model.domain.ErrorResponse
import java.lang.Exception

class ErrorResponseException(error: ErrorResponse) : Exception(error.message)
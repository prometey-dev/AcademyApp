package ru.prometeydev.movie.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler

open class BaseViewModel : ViewModel() {

    protected val mutableError = MutableLiveData<String>()

    val error: LiveData<String> get() = mutableError

    fun exceptionHandler() = CoroutineExceptionHandler { _, throwable ->
        mutableError.value = throwable.message
    }

}
package ru.prometeydev.movie.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler

open class BaseViewModel : ViewModel() {

    protected val mutableError = MutableLiveData<String>()
    protected val mutableStateLoading = MutableLiveData<StateLoading>(StateLoading.Default)

    val error: LiveData<String> get() = mutableError
    val stateLoading: LiveData<StateLoading> get() = mutableStateLoading

    fun exceptionHandler() = CoroutineExceptionHandler { _, throwable ->
        mutableError.value = throwable.message
        mutableStateLoading.value = StateLoading.Error
    }

}
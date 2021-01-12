package ru.prometeydev.movie.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.Exception

open class BaseViewModel<T> : ViewModel() {

    protected val mutableLiveData: MutableLiveData<Result<T>> = MutableLiveData()

    protected val mutableStateFlow: MutableStateFlow<Result<T>> = MutableStateFlow(Result.Loading)

    protected fun requestWithLiveData(
        request: suspend () -> T
    ) {
        viewModelScope.launch {
            try {
                mutableLiveData.postValue(Result.Loading)
                val response = request.invoke()
                mutableLiveData.postValue(Result.Success(response))
            } catch (ex: Throwable) {
                mutableLiveData.postValue(Result.Error(ex.message ?: ""))
            }
        }
    }

    protected fun requestWithStateFlow(
        request: () -> Flow<T>
    ) {
        viewModelScope.launch {
            try {
                mutableStateFlow.value = Result.Loading
                val response = request.invoke()
                response.collectLatest {
                    mutableStateFlow.value = Result.Success(it)
                }
            } catch (e: Exception) {
                mutableStateFlow.value = Result.Error(e.message ?: "")
            }
        }
    }

}
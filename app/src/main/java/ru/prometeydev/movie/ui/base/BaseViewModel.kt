package ru.prometeydev.movie.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.Exception

open class BaseViewModel<R> : ViewModel() {

    protected val mutableLiveData: MutableLiveData<R> = MutableLiveData()

    @Suppress("UNCHECKED_CAST")
    protected fun <T> requestWithLiveData(
        request: suspend () -> T
    ) {
        viewModelScope.launch {
            try {
                mutableLiveData.postValue(Result.Loading as R)
                val response = request.invoke()
                mutableLiveData.postValue(Result.Success(response) as R)
            } catch (ex: Throwable) {
                mutableLiveData.postValue(Result.Error(ex.message ?: "") as R)
            }
        }
    }

    protected fun <T> requestWithStateFlow(
        stateFlow: MutableStateFlow<Result>,
        request: () -> Flow<T>
    ) {
        viewModelScope.launch {
            try {
                stateFlow.value = Result.Loading
                val response = request.invoke()
                response.collectLatest {
                    stateFlow.value = Result.Success(it)
                }
            } catch (e: Exception) {
                stateFlow.value = Result.Error(e.message ?: "")
            }
        }
    }

}
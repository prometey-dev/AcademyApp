package ru.prometeydev.movie.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.Exception

open class BaseViewModel : ViewModel() {

    protected fun <T> requestWithLiveData(
        liveData: MutableLiveData<Result>,
        request: suspend () -> T
    ) {
        viewModelScope.launch {
            try {
                liveData.postValue(Result.Loading)
                val response = request.invoke()
                liveData.postValue(Result.Success(response))
            } catch (ex: Throwable) {
                liveData.postValue(Result.Error(ex.message))
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
                stateFlow.value = Result.Error(e.message)
            }
        }
    }

}
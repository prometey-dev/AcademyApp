package ru.prometeydev.movie.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.Exception

open class BaseViewModel : ViewModel() {

    fun <T> requestWithLiveData(
        liveData: MutableLiveData<Event<T>>,
        request: suspend () -> T
    ) {
        viewModelScope.launch {
            try {
                liveData.postValue(Event.loading())
                val response = request.invoke()
                liveData.postValue(Event.success(response))
            } catch (ex: Exception) {
                liveData.postValue(Event.error(ex.message))
            }
        }
    }

}
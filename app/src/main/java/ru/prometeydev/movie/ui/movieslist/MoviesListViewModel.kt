package ru.prometeydev.movie.ui.movieslist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.prometeydev.movie.model.Movie
import ru.prometeydev.movie.model.MoviesRepository
import ru.prometeydev.movie.ui.base.BaseViewModel
import ru.prometeydev.movie.ui.base.Event

class MoviesListViewModel(
    private val repository: MoviesRepository
): BaseViewModel() {

    private val _mutableLiveData = MutableLiveData<Event<List<Movie>>>()

    val liveData: LiveData<Event<List<Movie>>> get() = _mutableLiveData

    fun loadMovies() {
        if (_mutableLiveData.value?.data.isNullOrEmpty()) {
            requestWithLiveData(_mutableLiveData) {
                repository.loadMovies()
            }
        }
    }

}
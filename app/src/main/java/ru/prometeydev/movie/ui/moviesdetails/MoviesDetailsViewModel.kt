package ru.prometeydev.movie.ui.moviesdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.prometeydev.movie.data.Actor

class MoviesDetailsViewModel : ViewModel() {

    private val _mutableActorsListEmptyState = MutableLiveData<Boolean>(false)

    val actorsListEmptyState: LiveData<Boolean> get() = _mutableActorsListEmptyState

    fun checkActorsList(actors: List<Actor>) {
        _mutableActorsListEmptyState.value = actors.isNullOrEmpty()
    }

}
package ru.prometeydev.movie.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.annotation.ExperimentalCoilApi
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.prometeydev.movie.model.usecases.MoviesInteractor
import ru.prometeydev.movie.model.usecases.TopRatedMovieInteractor
import ru.prometeydev.movie.ui.MovieNotifications
import java.lang.Exception

class MoviesWorker(context: Context, params: WorkerParameters): CoroutineWorker(context, params), KoinComponent {

    private val moviesInteractor: MoviesInteractor by inject()
    private val topRatedMovieInteractor: TopRatedMovieInteractor by inject()
    private val notifications: MovieNotifications by inject()

    init {
        notifications.initialize()
    }

    @ExperimentalCoilApi
    override suspend fun doWork(): Result {
        return try {
            moviesInteractor.loadMoviesAndSave()
            topRatedMovieInteractor.getNewTopRatedMovie()?.let { movie ->
                notifications.show(movie)
            }
            Result.success()
        } catch (ex: Exception) {
            Result.failure()
        }
    }

}
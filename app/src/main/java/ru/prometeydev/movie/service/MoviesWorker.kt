package ru.prometeydev.movie.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.KoinComponent
import org.koin.core.inject
import ru.prometeydev.movie.model.MoviesRepository
import java.lang.Exception

class MoviesWorker(context: Context, params: WorkerParameters): CoroutineWorker(context, params), KoinComponent {

    private val repository: MoviesRepository by inject()

    override suspend fun doWork(): Result {
        return try {
            repository.loadMoviesAndSave()
            Result.success()
        } catch (ex: Exception) {
            Result.failure()
        }
    }

}
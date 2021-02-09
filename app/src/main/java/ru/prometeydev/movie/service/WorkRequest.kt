package ru.prometeydev.movie.service

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class WorkRequest(private val appContext: Context) {

    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .setRequiresCharging(true)
        .build()

    private val periodicRequest = PeriodicWorkRequest.Builder(
        MoviesWorker::class.java, REPEAT_INTERVAL, TimeUnit.MINUTES, FLEX_INTERVAL, TimeUnit.MINUTES
    ).setConstraints(constraints).build()

    fun startWorker() {
        WorkManager.getInstance(appContext).enqueueUniquePeriodicWork(
                MOVIES_PERIODIC_WORK, ExistingPeriodicWorkPolicy.REPLACE, periodicRequest
        )
    }

    companion object {
        private const val MOVIES_PERIODIC_WORK = "MOVIES_PERIODIC_WORK"
        private const val REPEAT_INTERVAL = 15L
        private const val FLEX_INTERVAL = 1L
    }

}
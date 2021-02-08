package ru.prometeydev.movie.service

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import java.util.concurrent.TimeUnit

class WorkRequest {

    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .setRequiresCharging(true)
        .build()

    val periodicRequest = PeriodicWorkRequest.Builder(
        MoviesWorker::class.java, REPEAT_INTERVAL, TimeUnit.MINUTES, FLEX_INTERVAL, TimeUnit.MINUTES
    ).setConstraints(constraints).build()

    companion object {
        const val MOVIES_PERIODIC_WORK = "MOVIES_PERIODIC_WORK"
        private const val REPEAT_INTERVAL = 15L
        private const val FLEX_INTERVAL = 1L
    }

}
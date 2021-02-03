package ru.prometeydev.movie.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.*
import org.koin.android.ext.android.inject
import ru.prometeydev.movie.R
import ru.prometeydev.movie.common.setAsRoot
import ru.prometeydev.movie.model.MoviesRepository
import ru.prometeydev.movie.service.MoviesWorker
import ru.prometeydev.movie.ui.movieslist.MoviesListFragment
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .setRequiresCharging(true)
        .build()

    private val periodicRequest = PeriodicWorkRequest.Builder(
        MoviesWorker::class.java, 15, TimeUnit.MINUTES, 1, TimeUnit.MINUTES
    ).setConstraints(constraints).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            setAsRoot(MoviesListFragment.instance())
        }

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            MOVIES_PERIODIC_WORK, ExistingPeriodicWorkPolicy.REPLACE, periodicRequest
        )
    }

    companion object {
        private const val MOVIES_PERIODIC_WORK = "MOVIES_PERIODIC_WORK"
    }

}
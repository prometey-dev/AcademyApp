package ru.prometeydev.movie.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.work.*
import ru.prometeydev.movie.R
import ru.prometeydev.movie.common.setAsRoot
import ru.prometeydev.movie.service.WorkRequest
import ru.prometeydev.movie.ui.movieslist.MoviesListFragment
import ru.prometeydev.movie.service.WorkRequest.Companion.MOVIES_PERIODIC_WORK

class MainActivity : AppCompatActivity() {

    private val workRequest = WorkRequest()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            setAsRoot(MoviesListFragment.instance())
        }

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            MOVIES_PERIODIC_WORK, ExistingPeriodicWorkPolicy.REPLACE, workRequest.periodicRequest
        )
    }

}
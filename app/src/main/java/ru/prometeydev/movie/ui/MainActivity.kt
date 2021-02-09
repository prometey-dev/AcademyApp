package ru.prometeydev.movie.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.prometeydev.movie.R
import ru.prometeydev.movie.common.setAsRoot
import ru.prometeydev.movie.service.WorkRequest
import ru.prometeydev.movie.ui.movieslist.MoviesListFragment

class MainActivity : AppCompatActivity() {

    private val workRequest = WorkRequest(applicationContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            setAsRoot(MoviesListFragment.instance())
            workRequest.startWorker()
        }
    }

}
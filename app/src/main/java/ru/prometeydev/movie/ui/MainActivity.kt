package ru.prometeydev.movie.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import ru.prometeydev.movie.R
import ru.prometeydev.movie.common.setAsRoot
import ru.prometeydev.movie.service.WorkRequest
import ru.prometeydev.movie.ui.moviesdetails.MoviesDetailsFragment
import ru.prometeydev.movie.ui.movieslist.MoviesListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            setAsRoot(MoviesListFragment.instance())
            WorkRequest.getInstance(applicationContext).start()
            intent?.let(::handleIntent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            intent.data?.lastPathSegment?.toIntOrNull()?.let {
                openMovie(it)
            }
        }
    }

    private fun openMovie(id: Int) {
        supportFragmentManager.popBackStack(FRAGMENT_MOVIE, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.commit {
            addToBackStack(FRAGMENT_MOVIE)
            replace(R.id.main_container, MoviesDetailsFragment.instance(id))
        }
    }

    companion object {
        private const val FRAGMENT_MOVIE = "movie"
    }

}
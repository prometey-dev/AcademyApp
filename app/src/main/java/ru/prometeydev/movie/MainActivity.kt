package ru.prometeydev.movie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.prometeydev.movie.common.setAsRoot

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            setAsRoot(FragmentMoviesList.instance())
        }
    }

}
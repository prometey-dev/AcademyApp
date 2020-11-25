package ru.prometeydev.movie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity(), MovieItemClickListener {

    var fragmentMoviesList: FragmentMoviesList? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            fragmentMoviesList = FragmentMoviesList()
            fragmentMoviesList?.apply {
                supportFragmentManager.beginTransaction()
                        .add(R.id.main_container, this)
                        .commit()
            }
        }
    }

    /**
     * Обработка onClick на элементе MovieItem, переход на FragmentMoviesDetails
     */
    override fun onMovieItemClicked() {
        supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.main_container, FragmentMoviesDetails())
                .commit()
    }

    /**
     * Переход назад с экрана деталей фильма к списку фильмов
     */
    override fun goBack() {
        fragmentMoviesList?.apply {
            supportFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.main_container, this)
                    .commit()
        }
    }

}
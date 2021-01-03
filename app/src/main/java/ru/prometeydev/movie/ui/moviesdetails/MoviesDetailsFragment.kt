package ru.prometeydev.movie.ui.moviesdetails

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.koin.android.viewmodel.ext.android.viewModel
import ru.prometeydev.movie.R
import ru.prometeydev.movie.common.popBack
import ru.prometeydev.movie.common.showMessage
import ru.prometeydev.movie.model.local.MovieDetails
import ru.prometeydev.movie.ui.base.BaseFragment
import ru.prometeydev.movie.ui.movieslist.calculateStarsCount

class MoviesDetailsFragment : BaseFragment() {

    private val viewModel: MoviesDetailsViewModel by viewModel()

    private var recycler: RecyclerView? = null
    private var buttonBack: TextView? = null
    private var movieBackdrop: ImageView? = null
    private var ageLimit: TextView? = null
    private var movieName: TextView? = null
    private var movieGenre: TextView? = null
    private var rating: RatingBar? = null
    private var reviewsCount: TextView? = null
    private var timeMovie: TextView? = null
    private var overview: TextView? = null

    override fun layoutId() = R.layout.fragment_movies_details

    override fun initViews(view: View) {
        buttonBack = view.findViewById(R.id.button_back)
        movieBackdrop = view.findViewById(R.id.movie_logo)
        ageLimit = view.findViewById(R.id.age_limit)
        movieName = view.findViewById<TextView>(R.id.movie_name)
        movieGenre = view.findViewById<TextView>(R.id.movie_genre)
        rating = view.findViewById<RatingBar>(R.id.rating)
        reviewsCount = view.findViewById<TextView>(R.id.reviews_count)
        timeMovie = view.findViewById<TextView>(R.id.time_movie)
        overview = view.findViewById<TextView>(R.id.description)

        recycler = view.findViewById<RecyclerView>(R.id.actor_list).apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

            adapter = ActorsAdapter()

            val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
            ContextCompat.getDrawable(context, R.drawable.shape_divider_actors)?.let {
                itemDecorator.setDrawable(it)
            }

            addItemDecoration(itemDecorator)
        }
    }

    override fun destroyViews() {
        buttonBack = null
        movieBackdrop = null
        ageLimit = null
        movieName = null
        movieGenre = null
        rating = null
        reviewsCount = null
        timeMovie = null
        overview = null
        recycler?.adapter = null
        recycler = null
    }

    override fun startObserve() {
        viewModel.liveData.observe(this.viewLifecycleOwner, this::setStateEvent)
    }

    override fun loadData() {
        arguments?.let {
            val movieId = it.getInt(MOVIE_ID)
            viewModel.loadMovie(movieId)
        }
    }

    override fun <T> bindViews(data: T) {
        val movie = data as MovieDetails

        buttonBack?.setOnClickListener {
            popBack()
        }
        movieBackdrop?.load(movie.backdrop)
        ageLimit?.text = getString(R.string.age_limit, movie.minimumAge)
        movieName?.text = movie.title
        movieGenre?.text = movie.genres.joinToString { it.name }
        rating?.rating = movie.ratings.calculateStarsCount()

        reviewsCount?.text = getString(R.string.reviews, movie.numberOfRatings)

        timeMovie?.text = getString(R.string.movie_time, movie.runtime)

        overview?.text = movie.overview

        (recycler?.adapter as? ActorsAdapter)?.apply {
            bindActors(movie.actors)
        }

        showMessageIfNeeded(movie.actors.isNullOrEmpty())
    }

    private fun showMessageIfNeeded(hasNoActors: Boolean) {
        if (hasNoActors) {
            showMessage(getString(R.string.actors_not_laded_message))
        }
    }

    companion object {

        fun instance(movieId: Int) = MoviesDetailsFragment().apply {
            arguments = Bundle().apply {
                putInt(MOVIE_ID, movieId)
            }
        }

        const val MOVIE_ID = "MOVIE_ID"

    }

}
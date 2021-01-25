package ru.prometeydev.movie.ui.moviesdetails

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import ru.prometeydev.movie.R
import ru.prometeydev.movie.common.popBack
import ru.prometeydev.movie.model.domain.Movie
import ru.prometeydev.movie.ui.base.BaseFragment
import ru.prometeydev.movie.ui.movieslist.calculateStarsCount

class MoviesDetailsFragment : BaseFragment<Movie>() {

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
        movieName = view.findViewById(R.id.movie_name)
        movieGenre = view.findViewById(R.id.movie_genre)
        rating = view.findViewById(R.id.rating)
        reviewsCount = view.findViewById(R.id.reviews_count)
        timeMovie = view.findViewById(R.id.time_movie)
        overview = view.findViewById(R.id.description)

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
        job?.cancel()
        job = lifecycleScope.launch {
            viewModel.stateFlow.collectLatest {
                this@MoviesDetailsFragment.setStateEvent(it)
            }
        }
    }

    override fun loadData() {
        arguments?.let {
            val movieId = it.getInt(MOVIE_ID)
            viewModel.loadMovie(movieId)
        }
    }

    override fun bindViews(data: Movie) {
        buttonBack?.setOnClickListener {
            popBack()
        }
        movieBackdrop?.load(data.backdrop)
        ageLimit?.text = getString(R.string.age_limit, data.minimumAge)
        movieName?.text = data.title
        movieGenre?.text = data.genres.joinToString { it.name }
        rating?.rating = data.ratings.calculateStarsCount()

        reviewsCount?.text = getString(R.string.reviews, data.numberOfRatings)

        timeMovie?.apply {
            this.text = getString(R.string.movie_time, data.runtime)
            this.isVisible = data.runtime != null
        }

        overview?.text = data.overview

        (recycler?.adapter as? ActorsAdapter)?.apply {
            bindActors(data.actors)
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
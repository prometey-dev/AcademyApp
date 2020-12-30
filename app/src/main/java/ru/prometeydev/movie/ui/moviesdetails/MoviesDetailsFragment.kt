package ru.prometeydev.movie.ui.moviesdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.prometeydev.movie.R
import ru.prometeydev.movie.ViewModelProviderFactory
import ru.prometeydev.movie.common.popBack
import ru.prometeydev.movie.common.showMessage
import ru.prometeydev.movie.model.MovieDetails
import ru.prometeydev.movie.ui.base.BaseFragment
import ru.prometeydev.movie.ui.movieslist.calculateStarsCount

class MoviesDetailsFragment : BaseFragment() {

    private val viewModel: MoviesDetailsViewModel by viewModels { ViewModelProviderFactory() }

    private var recycler: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_movies_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.movieState.observe(this.viewLifecycleOwner) { movie ->
            setupViews(view, movie)
        }
        viewModel.error.observe(this.viewLifecycleOwner, this::onError)
        viewModel.stateLoading.observe(this.viewLifecycleOwner, this::handleLoading)

        loadData()
    }

    override fun onDestroyView() {
        recycler?.adapter = null
        recycler = null

        super.onDestroyView()
    }

    private fun loadData() {
        arguments?.let {
            val movieId = it.getInt(MOVIE_ID)
            viewModel.loadMovie(movieId)
        }
    }

    private fun setupViews(view: View, movie: MovieDetails) {
        view.findViewById<TextView>(R.id.button_back)
            .setOnClickListener {
                popBack()
            }

        view.findViewById<ImageView>(R.id.movie_logo)
            .load(movie.backdrop)

        view.findViewById<TextView>(R.id.age_limit)
            .text = getString(R.string.age_limit, movie.minimumAge)

        view.findViewById<TextView>(R.id.movie_name).text = movie.title
        view.findViewById<TextView>(R.id.movie_genre).text = movie.genres.joinToString { it.name }
        view.findViewById<RatingBar>(R.id.rating).rating = movie.ratings.calculateStarsCount()

        view.findViewById<TextView>(R.id.reviews_count)
            .text = getString(R.string.reviews, movie.numberOfRatings)

        view.findViewById<TextView>(R.id.time_movie).text = getString(R.string.movie_time, movie.runtime)

        view.findViewById<TextView>(R.id.description).text = movie.overview

        recycler = view.findViewById<RecyclerView>(R.id.actor_list).apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

            adapter = ActorsAdapter()

            val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
            ContextCompat.getDrawable(context, R.drawable.shape_divider_actors)?.let {
                itemDecorator.setDrawable(it)
            }

            addItemDecoration(itemDecorator)
        }

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


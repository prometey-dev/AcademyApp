package ru.prometeydev.movie.ui.moviesdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.prometeydev.movie.R
import ru.prometeydev.movie.data.adapters.ActorsAdapter
import ru.prometeydev.movie.data.Movie
import ru.prometeydev.movie.data.adapters.calculateStarsCount

class MoviesDetailsFragment : MoviesDetailsNavigable() {

    private var recycler: RecyclerView? = null

    private var movie: Movie? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_movies_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()
        movie?.let { setupViews(view, it) }
        viewModel.actorsListEmptyState.observe(this.viewLifecycleOwner, this::showMessageIfNeeded)
    }

    override fun onStart() {
        super.onStart()

        loadActors()
    }

    override fun onDestroyView() {
        recycler?.adapter = null
        recycler = null
        movie = null

        super.onDestroyView()
    }

    private fun loadData() {
        arguments?.let {
            movie = it.getParcelable(MOVIE)
        }
    }

    private fun setupViews(view: View, movie: Movie) {
        view.findViewById<TextView>(R.id.button_back)
            .setOnClickListener {
                goBack()
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

        viewModel.checkActorsList(movie.actors)
    }

    private fun loadActors() {
        (recycler?.adapter as? ActorsAdapter)?.apply {
            movie?.let { bindActors(it.actors) }
        }
    }

    private fun showMessageIfNeeded(isNotActors: Boolean) {
        if (isNotActors) {
            showMessage(getString(R.string.actors_not_laded_message))
        }
    }

    companion object {

        fun instance(movie: Movie) = MoviesDetailsFragment().apply {
            arguments = Bundle().apply {
                putParcelable(MOVIE, movie)
            }
        }

        const val MOVIE = "MOVIE"

    }

}


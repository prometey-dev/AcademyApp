package ru.prometeydev.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.prometeydev.movie.data.adapters.ActorsAdapter
import ru.prometeydev.movie.data.domain.MoviesDataSource
import ru.prometeydev.movie.data.models.Actor
import ru.prometeydev.movie.data.models.Movie
import ru.prometeydev.movie.data.models.MovieAdditional

class FragmentMoviesDetails : Fragment() {

    private var recycler: RecyclerView? = null

    private var movie: Movie? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_movies_details, container, false)

        view.findViewById<TextView>(R.id.button_back)
            .setOnClickListener {
                activity?.let {
                    it.supportFragmentManager.popBackStack()
                }
            }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadData()

        view.findViewById<ImageView>(R.id.movie_logo)
            .setImageResource(movie?.additional!!.pictureDrawable)

        view.findViewById<TextView>(R.id.age_limit)
            .text = context?.getString(R.string.age_limit, movie?.ageLimit)

        view.findViewById<TextView>(R.id.movie_name).text = movie?.name
        view.findViewById<TextView>(R.id.movie_genre).text = movie?.genre
        view.findViewById<RatingBar>(R.id.rating).rating = movie?.rating ?: 0f

        view.findViewById<TextView>(R.id.reviews_count)
            .text = context?.getString(R.string.reviews, movie?.reviewsCount)

        view.findViewById<TextView>(R.id.description).text = movie?.additional!!.storyLine

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

    override fun onStart() {
        super.onStart()

        loadActors()
    }

    override fun onDetach() {
        recycler = null
        movie = null

        super.onDetach()
    }

    private fun loadData() {
        arguments?.let {
            val movieId = it.getLong(MOVIE_ID)
            movie = MoviesDataSource().getMovies().firstOrNull { it.id == movieId }
        }
    }

    private fun loadActors() {
        (recycler?.adapter as? ActorsAdapter)?.apply {
            bindActors(movie?.additional!!.actors)
        }
    }

    companion object {

        fun instance(movie: Movie) = FragmentMoviesDetails().apply {
            arguments = Bundle().apply {
                putLong(MOVIE_ID, movie.id)
            }
        }

        const val MOVIE_ID = "MOVIE_ID"

    }

}


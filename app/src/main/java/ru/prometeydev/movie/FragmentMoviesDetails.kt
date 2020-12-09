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
import coil.load
import com.google.android.material.snackbar.Snackbar
import ru.prometeydev.movie.data.adapters.ActorsAdapter
import ru.prometeydev.movie.data.Movie

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
            .load(movie?.backdrop)

        view.findViewById<TextView>(R.id.age_limit)
            .text = context?.getString(R.string.age_limit, movie?.minimumAge)

        view.findViewById<TextView>(R.id.movie_name).text = movie?.title
        view.findViewById<TextView>(R.id.movie_genre).text = movie?.genres?.joinToString { it.name }
        view.findViewById<RatingBar>(R.id.rating).rating = movie?.ratings ?: 0f

        view.findViewById<TextView>(R.id.reviews_count)
            .text = context?.getString(R.string.reviews, movie?.numberOfRatings)

        view.findViewById<TextView>(R.id.description).text = movie?.overview

        recycler = view.findViewById<RecyclerView>(R.id.actor_list).apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

            adapter = ActorsAdapter()

            val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
            ContextCompat.getDrawable(context, R.drawable.shape_divider_actors)?.let {
                itemDecorator.setDrawable(it)
            }

            addItemDecoration(itemDecorator)
        }

        if (movie?.actors.isNullOrEmpty()) {
            Snackbar.make(view, R.string.actors_not_laded_message, Snackbar.LENGTH_LONG).show()
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
            movie = it.getParcelable(MOVIE)
        }
    }

    private fun loadActors() {
        (recycler?.adapter as? ActorsAdapter)?.apply {
            movie?.let { bindActors(it.actors) }
        }
    }

    companion object {

        fun instance(movie: Movie) = FragmentMoviesDetails().apply {
            arguments = Bundle().apply {
                putParcelable(MOVIE, movie)
            }
        }

        const val MOVIE = "MOVIE"

    }

}


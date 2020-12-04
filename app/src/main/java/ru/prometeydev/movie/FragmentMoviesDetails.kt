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

            // add ItemDecoration (spaces between items)
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

    // загрузка данных
    private fun loadData() {
        arguments?.let {
            movie = Movie(
                id = it.getLong(MOVIE_ID),
                name = it.getString(MOVIE_NAME, ""),
                genre = it.getString(MOVIE_GENRE, ""),
                rating = it.getFloat(RATING),
                reviewsCount = it.getInt(REVIEWS_COUNT),
                ageLimit = it.getInt(AGE_LIMIT),
                filmCoverDrawable = it.getInt(COVER),

                additional = MovieAdditional(
                    pictureDrawable =  it.getInt(MOVIE_PICTURE),
                    storyLine = it.getString(STORY_LINE, ""),
                    actors = it.getParcelableArrayList<Actor>(ACTORS_LIST)?.let { actors -> actors } ?: arrayListOf()
                )
            )
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
                putString(MOVIE_NAME, movie.name)
                putString(MOVIE_GENRE, movie.genre)
                putFloat(RATING, movie.rating)
                putInt(REVIEWS_COUNT, movie.reviewsCount)
                putInt(AGE_LIMIT, movie.ageLimit)
                putInt(MOVIE_PICTURE, movie.additional!!.pictureDrawable)
                putString(STORY_LINE, movie.additional.storyLine)
                putParcelableArrayList(ACTORS_LIST, movie.additional.actors)
                putInt(COVER, movie.filmCoverDrawable)
            }
        }

        const val MOVIE_ID = "MOVIE_ID"
        const val MOVIE_NAME = "MOVIE_NAME"
        const val MOVIE_GENRE = "GENRE"
        const val RATING = "RATING"
        const val REVIEWS_COUNT = "REVIEWS_COUNT"
        const val AGE_LIMIT = "AGE_LIMIT"
        const val STORY_LINE = "STORY_LINE"
        const val MOVIE_PICTURE = "MOVIE_PICTURE"
        const val ACTORS_LIST = "ACTORS_LIST"
        const val COVER = "COVER"
    }

}


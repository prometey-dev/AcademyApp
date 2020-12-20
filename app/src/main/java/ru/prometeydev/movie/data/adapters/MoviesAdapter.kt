package ru.prometeydev.movie.data.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import kotlinx.coroutines.*
import ru.prometeydev.movie.R
import ru.prometeydev.movie.data.Movie

/**
 * Адаптер для списка фильмов
 */
class MoviesAdapter(
    private val clickListener: OnRecyclerItemClicked
) : RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder>() {

    private var movies = listOf<Movie>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        return MoviesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_holder_movie, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        holder.onBind(movies[position], clickListener)
    }

    override fun getItemCount(): Int = movies.size

    fun bindMovies(movies: List<Movie>) {
        this.movies = movies
        notifyDataSetChanged()
    }

    /**
     * Интерфейс для обработки нажатия на элементе списка
     */
    interface OnRecyclerItemClicked {
        fun onClick(movie: Movie)
    }

    /**
     * Холдер для списка фильмов
     */
    class MoviesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private var scope = CoroutineScope(Dispatchers.Default + Job())

        private val name = itemView.findViewById<TextView>(R.id.movie_name)
        private val genre = itemView.findViewById<TextView>(R.id.movie_genre)
        private val duration = itemView.findViewById<TextView>(R.id.time_movie)
        private val rating = itemView.findViewById<RatingBar>(R.id.rating)
        private val reviewsCount = itemView.findViewById<TextView>(R.id.reviews_count)
        private val ageLimit = itemView.findViewById<TextView>(R.id.age_limit)
        private val filmCover = itemView.findViewById<ImageView>(R.id.movie_item_image)
        private val like = itemView.findViewById<ImageView>(R.id.like_heart)

        fun onBind(movie: Movie, clickListener: OnRecyclerItemClicked) {
            name.text = movie.title
            genre.text = movie.genres.joinToString { it.name }
            duration.text = context.getString(R.string.movie_time, movie.runtime)
            rating.rating = movie.ratings.calculateStarsCount()
            reviewsCount.text = context.getString(R.string.reviews, movie.numberOfRatings)
            ageLimit.text  = context.getString(R.string.age_limit, movie.minimumAge)

            filmCover.load(movie.poster) {
                crossfade(true)
                placeholder(R.drawable.ic_image_placeholder)
                fallback(R.drawable.ic_image_placeholder)
            }

            itemView.setOnClickListener {
                clickListener.onClick(movie)
            }
        }

    }

}

private val RecyclerView.ViewHolder.context
    get() = this.itemView.context

fun Float.calculateStarsCount(): Float = this / 2
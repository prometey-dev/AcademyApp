package ru.prometeydev.movie.ui.movieslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.prometeydev.movie.R
import ru.prometeydev.movie.model.local.Movie

/**
 * Адаптер для списка фильмов
 */
class MoviesAdapter(
    private val clickListener: OnRecyclerItemClicked
) : PagingDataAdapter<Movie, MoviesAdapter.MoviesViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        return MoviesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_holder_movie, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        getItem(position)?.let { holder.onBind(it, clickListener) }
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

        private val name = itemView.findViewById<TextView>(R.id.movie_name)
        private val genre = itemView.findViewById<TextView>(R.id.movie_genre)
        private val rating = itemView.findViewById<RatingBar>(R.id.rating)
        private val reviewsCount = itemView.findViewById<TextView>(R.id.reviews_count)
        private val ageLimit = itemView.findViewById<TextView>(R.id.age_limit)
        private val filmCover = itemView.findViewById<ImageView>(R.id.movie_item_image)
        private val like = itemView.findViewById<ImageView>(R.id.like_heart)

        fun onBind(movie: Movie, clickListener: OnRecyclerItemClicked) {
            name.text = movie.title
            genre.text = movie.genres.joinToString { it.name }
            rating.rating = movie.ratings.calculateStarsCount()
            reviewsCount.text = context.getString(R.string.reviews, movie.numberOfRatings)
            ageLimit.text  = context.getString(R.string.age_limit, movie.minimumAge)

            filmCover.load(movie.poster) {
                crossfade(true)
                placeholder(R.drawable.ic_image_placeholder)
                fallback(R.drawable.ic_image_placeholder)
                error(R.drawable.ic_image_placeholder)
            }

            itemView.setOnClickListener {
                clickListener.onClick(movie)
            }
        }

    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }
        }
    }

}

private val RecyclerView.ViewHolder.context
    get() = this.itemView.context

fun Float.calculateStarsCount(): Float = this / 2
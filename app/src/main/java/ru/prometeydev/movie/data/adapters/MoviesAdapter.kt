package ru.prometeydev.movie.data.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.prometeydev.movie.R
import ru.prometeydev.movie.data.models.Movie

class MoviesAdapter(
    private val clickListener: OnRecyclerItemClicked
) : RecyclerView.Adapter<MoviesViewHolder>() {

    private var movies = listOf<Movie>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        return MoviesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_holder_movie, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        holder.onBind(movies[position])
        holder.itemView.setOnClickListener {
            clickListener.onClick(movies[position])
        }
    }

    override fun getItemCount(): Int = movies.size

    fun bindMovies(movies: List<Movie>) {
        this.movies = movies
        notifyDataSetChanged()
    }

    interface OnRecyclerItemClicked {
        fun onClick(movie: Movie)
    }

}

class MoviesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val name = itemView.findViewById<TextView>(R.id.movie_name)
    private val genre = itemView.findViewById<TextView>(R.id.movie_genre)
    private val duration = itemView.findViewById<TextView>(R.id.time_movie)
    private val rating = itemView.findViewById<RatingBar>(R.id.rating)
    private val reviewsCount = itemView.findViewById<TextView>(R.id.reviews_count)
    private val ageLimit = itemView.findViewById<TextView>(R.id.age_limit)
    private val filmCover = itemView.findViewById<ImageView>(R.id.movie_item_image)
    private val like = itemView.findViewById<ImageView>(R.id.like_heart)

    fun onBind(movie: Movie) {
        name.text = movie.name
        genre.text = movie.genre
        duration.text = context.getString(R.string.movie_time, movie.duration.toString())
        rating.rating = movie.rating
        reviewsCount.text = context.getString(R.string.reviews, movie.reviewsCount.toString())
        ageLimit.text  = context.getString(R.string.age_limit, movie.ageLimit)
        filmCover.setImageResource(movie.filmCoverDrawable)
        like.setImageResource(
            if (movie.hasLike)
                R.drawable.ic_like_active
            else
                R.drawable.ic_like_inactive
        )
    }

}

private val RecyclerView.ViewHolder.context
    get() = this.itemView.context
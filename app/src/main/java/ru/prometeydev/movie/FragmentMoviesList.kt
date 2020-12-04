package ru.prometeydev.movie

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ru.prometeydev.movie.data.adapters.MoviesAdapter
import ru.prometeydev.movie.data.domain.MoviesDataSource
import ru.prometeydev.movie.data.models.Movie

class FragmentMoviesList : Fragment() {

    private var recycler: RecyclerView? = null

    private var movies: List<Movie>? = null

    private var spanCount = VERTICAL_SPAN_COUNT

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        spanCount = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            VERTICAL_SPAN_COUNT else HORIZONTAL_SPAN_COUNT

        return inflater.inflate(R.layout.fragment_movies_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler = view.findViewById(R.id.movie_list)
        recycler?.apply {
            layoutManager = GridLayoutManager(context, spanCount)
            adapter = MoviesAdapter(clickListener)
        }
    }

    override fun onStart() {
        super.onStart()

        movies = MoviesDataSource().getMovies()
        loadData()
    }

    override fun onDetach() {
        recycler = null
        movies = null

        super.onDetach()
    }

    private fun loadData() {
        (recycler?.adapter as? MoviesAdapter)?.apply {
            movies?.let { bindMovies(it) }
        }
    }

    private fun doOnClick(movie: Movie) {
        if (!movie.hasFullInfo()) {
            view?.let { showMessageMissed(it) }
            return
        }

        activity?.let {
            it.supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.main_container, FragmentMoviesDetails.instance(movie))
                .commit()
        }
    }

    private fun showMessageMissed(view: View) {
        context?.let {
            Snackbar.make(view, "Information missed", Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(it, R.color.black))
                .setTextColor(ContextCompat.getColor(it, R.color.star_put_color))
                .show()
        }
    }

    private val clickListener = object : MoviesAdapter.OnRecyclerItemClicked {
        override fun onClick(movie: Movie) = doOnClick(movie)
    }

    companion object {

        fun instance() = FragmentMoviesList()

        const val VERTICAL_SPAN_COUNT = 2
        const val HORIZONTAL_SPAN_COUNT = 4
    }

}
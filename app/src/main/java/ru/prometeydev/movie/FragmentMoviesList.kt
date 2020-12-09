package ru.prometeydev.movie

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import ru.prometeydev.movie.data.adapters.MoviesAdapter
import ru.prometeydev.movie.data.Movie
import ru.prometeydev.movie.data.loadMovies

class FragmentMoviesList : Fragment() {

    private var scope = CoroutineScope(Dispatchers.Default + Job())

    private var recycler: RecyclerView? = null

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

        scope.launch {
            context?.let {
                loadData(it)
            }
        }
    }

    override fun onDetach() {
        recycler = null

        super.onDetach()
    }

    private suspend fun loadData(context: Context) = withContext(Dispatchers.Main) {
        (recycler?.adapter as? MoviesAdapter)?.apply {
            val movies = loadMovies(context)
            bindMovies(movies)
        }
    }

    private fun doOnClick(movie: Movie) {
        activity?.let {
            it.supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.main_container, FragmentMoviesDetails.instance(movie))
                .commit()
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
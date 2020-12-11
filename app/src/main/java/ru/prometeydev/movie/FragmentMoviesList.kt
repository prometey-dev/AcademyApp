package ru.prometeydev.movie

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
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

    private var savedRecyclerLayoutState: Parcelable? = null

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        if (isStateSaved) {
            recycler?.let {
                outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, it.layoutManager?.onSaveInstanceState())
            }
        } else {
            arguments = outState
            recycler?.let {
                arguments?.putParcelable(BUNDLE_RECYCLER_LAYOUT, it.layoutManager?.onSaveInstanceState())
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT)
        } else {
            arguments?.let {
                savedRecyclerLayoutState = it.getParcelable(BUNDLE_RECYCLER_LAYOUT)
            }
        }
    }

    private suspend fun loadData(context: Context) = withContext(Dispatchers.Main) {
        val movies = loadMovies(context)
        (recycler?.adapter as? MoviesAdapter)?.apply {
            bindMovies(movies)
        }
        recycler?.layoutManager?.onRestoreInstanceState(savedRecyclerLayoutState)
    }

    private fun doOnClick(movie: Movie) {
        onSaveInstanceState(Bundle())

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

        const val BUNDLE_RECYCLER_LAYOUT = "BUNDLE_RECYCLER_LAYOUT "
    }

}
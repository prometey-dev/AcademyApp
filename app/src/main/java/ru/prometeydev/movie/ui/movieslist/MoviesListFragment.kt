package ru.prometeydev.movie.ui.movieslist

import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.prometeydev.movie.R
import ru.prometeydev.movie.ViewModelProviderFactory
import ru.prometeydev.movie.data.adapters.MoviesAdapter
import ru.prometeydev.movie.data.Movie

class MoviesListFragment : MoviesListNavigable<MoviesListViewModel>() {

    private val viewModel: MoviesListViewModel by viewModels { ViewModelProviderFactory() }

    private var moviesAdapter: MoviesAdapter? = null

    private var recycler: RecyclerView? = null

    private var spanCount = VERTICAL_SPAN_COUNT

    private var savedRecyclerLayoutState: Parcelable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_movies_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)

        viewModel.moviesListState.observe(this.viewLifecycleOwner, this::loadData)
    }

    override fun onStart() {
        super.onStart()

        context?.let {
            viewModel.updateMovies(it)
        }
    }

    override fun onDestroyView() {
        recycler?.adapter = null
        recycler = null

        super.onDestroyView()
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

    private fun setupViews(view: View) {
        spanCount = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            VERTICAL_SPAN_COUNT else HORIZONTAL_SPAN_COUNT

        moviesAdapter = MoviesAdapter(clickListener)
        recycler = view.findViewById(R.id.movie_list)
        recycler?.apply {
            layoutManager = GridLayoutManager(context, spanCount)
            adapter = moviesAdapter
        }
    }

    private fun loadData(movies: List<Movie>) {
        moviesAdapter?.bindMovies(movies)
    }

    private fun doOnClick(movie: Movie) {
        onSaveInstanceState(Bundle())

        openMovieDetails(movie)
    }

    private val clickListener = object : MoviesAdapter.OnRecyclerItemClicked {
        override fun onClick(movie: Movie) = doOnClick(movie)
    }

    companion object {

        fun instance() = MoviesListFragment()

        const val VERTICAL_SPAN_COUNT = 2
        const val HORIZONTAL_SPAN_COUNT = 4

        const val BUNDLE_RECYCLER_LAYOUT = "BUNDLE_RECYCLER_LAYOUT "
    }

}
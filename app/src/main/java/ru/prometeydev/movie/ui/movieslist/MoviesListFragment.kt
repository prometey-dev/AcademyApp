package ru.prometeydev.movie.ui.movieslist

import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.android.viewmodel.ext.android.viewModel
import ru.prometeydev.movie.R
import ru.prometeydev.movie.common.show
import ru.prometeydev.movie.model.Movie
import ru.prometeydev.movie.ui.base.BaseFragment
import ru.prometeydev.movie.ui.moviesdetails.MoviesDetailsFragment

class MoviesListFragment : BaseFragment() {

    private val viewModel: MoviesListViewModel by viewModel()

    private var moviesAdapter: MoviesAdapter? = null
    private var recycler: RecyclerView? = null
    private var spanCount = VERTICAL_SPAN_COUNT
    private var savedRecyclerLayoutState: Parcelable? = null

    override fun layoutId() = R.layout.fragment_movies_list

    override fun initViews(view: View) {
        spanCount = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            VERTICAL_SPAN_COUNT else HORIZONTAL_SPAN_COUNT

        moviesAdapter = MoviesAdapter(clickListener)
        recycler = view.findViewById(R.id.movie_list)
        recycler?.apply {
            layoutManager = GridLayoutManager(context, spanCount)
            adapter = moviesAdapter
        }
    }

    override fun destroyViews() {
        recycler?.adapter = null
        recycler = null
    }

    override fun startObserve() {
        viewModel.liveData.observe(this.viewLifecycleOwner, this::setStateEvent)
    }

    override fun loadData() {
        viewModel.loadMovies()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> bindViews(data: T) {
        val movies = data as List<Movie>
        moviesAdapter?.bindMovies(movies)
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

    private fun doOnClick(movie: Movie) {
        onSaveInstanceState(Bundle())

        show(MoviesDetailsFragment.instance(movie.id))
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
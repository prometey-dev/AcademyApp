package ru.prometeydev.movie.ui.movieslist

import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.Button
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import ru.prometeydev.movie.R
import ru.prometeydev.movie.common.show
import ru.prometeydev.movie.common.showMessage
import ru.prometeydev.movie.model.local.Movie
import ru.prometeydev.movie.ui.base.BaseFragment
import ru.prometeydev.movie.ui.moviesdetails.MoviesDetailsFragment


class MoviesListFragment : BaseFragment<PagingData<Movie>>() {

    private val viewModel: MoviesListViewModel by viewModel()

    private var moviesAdapter: MoviesAdapter? = null
    private var recycler: RecyclerView? = null
    private var retryButton: Button? = null
    private var spanCount = VERTICAL_SPAN_COUNT
    private var savedRecyclerLayoutState: Parcelable? = null

    override fun layoutId() = R.layout.fragment_movies_list

    override fun initViews(view: View) {
        spanCount = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            VERTICAL_SPAN_COUNT else HORIZONTAL_SPAN_COUNT

        moviesAdapter = MoviesAdapter(clickListener)
        recycler = view.findViewById(R.id.movie_list)

        moviesAdapter?.addLoadStateListener { loadState ->
            addLoadState(loadState)
        }

        recycler?.apply {
            layoutManager = GridLayoutManager(context, spanCount)
            adapter = moviesAdapter?.withLoadStateFooter(
                    footer = MoviesLoadStateAdapter { moviesAdapter?.retry() }
            )
        }

        retryButton = view.findViewById(R.id.retry_button)
        retryButton?.setOnClickListener { moviesAdapter?.retry() }
    }

    override fun destroyViews() {
        recycler?.adapter = null
        recycler = null
        retryButton = null
    }

    override fun startObserve() {
        job?.cancel()
        job = lifecycleScope.launch {
            viewModel.stateFlow.collectLatest {
                this@MoviesListFragment.setStateEvent(it)
            }
        }
    }

    override fun loadData() {
        viewModel.loadMovies()
    }

    override fun bindViews(data: PagingData<Movie>) {
        lifecycleScope.launch {
            moviesAdapter?.submitData(data)
        }
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

    private fun addLoadState(loadState: CombinedLoadStates) {
        loader?.isVisible = loadState.source.refresh is LoadState.Loading
        retryButton?.isVisible = loadState.source.refresh is LoadState.Error

        val errorState = loadState.source.append as? LoadState.Error
                ?:loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error

        errorState?.let { showMessage(it.error.message ?: "") }
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
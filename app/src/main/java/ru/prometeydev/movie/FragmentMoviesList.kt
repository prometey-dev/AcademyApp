package ru.prometeydev.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.prometeydev.movie.data.adapters.MoviesAdapter
import ru.prometeydev.movie.data.domain.MoviesDataSource
import ru.prometeydev.movie.data.models.Movie

class FragmentMoviesList : Fragment() {

    private var recycler: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_movies_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler = view.findViewById(R.id.movie_list)
        recycler?.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = MoviesAdapter(clickListener)
        }
    }

    override fun onStart() {
        super.onStart()

        loadData()
    }

    override fun onDetach() {
        recycler = null

        super.onDetach()
    }

    private fun loadData() {
        (recycler?.adapter as? MoviesAdapter)?.apply {
            bindMovies(MoviesDataSource().getMovies())
        }
    }

    private fun doOnClick() {
        recycler?.let {
            activity?.let {
                it.supportFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.main_container, FragmentMoviesDetails.instance())
                    .commit()
            }
        }
    }

    private val clickListener = object : MoviesAdapter.OnRecyclerItemClicked {
        override fun onClick(movie: Movie) = doOnClick()
    }

    companion object {
        fun instance() = FragmentMoviesList()
    }

}
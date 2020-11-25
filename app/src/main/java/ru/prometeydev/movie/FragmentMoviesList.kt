package ru.prometeydev.movie

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment

class FragmentMoviesList : Fragment() {

    private var movieItemClickListener: MovieItemClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_movies_list, container, false)

        view?.findViewById<ImageView>(R.id.movie_main_item)?.apply {
            setOnClickListener {
                movieItemClickListener?.onMovieItemClicked()
            }
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MovieItemClickListener) {
            movieItemClickListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        movieItemClickListener = null
    }

}
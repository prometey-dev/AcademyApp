package ru.prometeydev.movie

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class FragmentMoviesDetails : Fragment() {

    private var movieItemClickListener: MovieItemClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_movies_details, container,false )

        view?.findViewById<TextView>(R.id.button_back)?.apply {
            setOnClickListener {
                movieItemClickListener?.goBack()
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
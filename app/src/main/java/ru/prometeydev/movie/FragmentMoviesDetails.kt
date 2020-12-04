package ru.prometeydev.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.prometeydev.movie.data.adapters.ActorsAdapter
import ru.prometeydev.movie.data.domain.ActorsDataSource

class FragmentMoviesDetails : Fragment() {

    private var recycler: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_movies_details, container, false)

        view.findViewById<TextView>(R.id.button_back)
            .setOnClickListener {
                activity?.let {
                    it.supportFragmentManager.popBackStack()
                }
            }

        view.findViewById<TextView>(R.id.age_limit)
                .text = context?.getString(R.string.age_limit, 13)

        view.findViewById<TextView>(R.id.reviews_count)
                .text = context?.getString(R.string.reviews)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler = view.findViewById(R.id.actor_list)
        recycler?.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)

            adapter = ActorsAdapter()

            // add ItemDecoration (spaces between items)
            val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
            ContextCompat.getDrawable(context, R.drawable.shape_divider_actors)?.let {
                itemDecorator.setDrawable(it)
            }

            addItemDecoration(itemDecorator)
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
        (recycler?.adapter as? ActorsAdapter)?.apply {
            bindActors(ActorsDataSource().getActors())
        }
    }

    companion object {
        fun instance() = FragmentMoviesDetails()
    }

}
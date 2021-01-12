package ru.prometeydev.movie.ui.movieslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import ru.prometeydev.movie.R

class MoviesLoadStateViewHolder(
        itemView: View,
        retry: () -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val loader = itemView.findViewById<ProgressBar>(R.id.loader)
    private val retryButton = itemView.findViewById<Button>(R.id.retry_button)

    init {
        retryButton.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        loader.isVisible = loadState is LoadState.Loading
        retryButton.isVisible = loadState !is LoadState.Loading
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): MoviesLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_holder_load_state_footer, parent, false)
            return MoviesLoadStateViewHolder(view, retry)
        }
    }

}
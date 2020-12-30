package ru.prometeydev.movie.ui.moviesdetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.prometeydev.movie.R
import ru.prometeydev.movie.model.Actor

/**
 * Адаптер для списка актеров
 */
class ActorsAdapter : RecyclerView.Adapter<ActorsAdapter.ActorsViewHolder>() {

    private var actors = listOf<Actor>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_actor, parent, false)

        return ActorsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ActorsViewHolder, position: Int) {
        holder.onBind(actors[position])
    }

    override fun getItemCount(): Int = actors.size

    fun bindActors(actors: List<Actor>) {
        this.actors = actors
        notifyDataSetChanged()
    }

    /**
     * Холдер для списка актеров
     */
    class ActorsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val avatar = itemView.findViewById<ImageView>(R.id.avatar)
        private val name = itemView.findViewById<TextView>(R.id.actor_name)

        fun onBind(actor: Actor) {
            avatar.load(actor.picture) {
                crossfade(true)
                placeholder(R.drawable.ic_image_placeholder)
                fallback(R.drawable.ic_image_placeholder)
                error(R.drawable.ic_image_placeholder)
            }

            name.text = actor.name
        }

    }

}


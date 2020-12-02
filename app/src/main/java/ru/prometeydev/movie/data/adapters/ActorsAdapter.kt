package ru.prometeydev.movie.data.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.prometeydev.movie.R
import ru.prometeydev.movie.data.models.Actor

/**
 * Адаптер для списка актеров
 */
class ActorsAdapter : RecyclerView.Adapter<ActorsAdapter.ActorsViewHolder>() {

    private var actors = listOf<Actor>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_actor, parent, false)

        // Вычисляем ширину разделителя между элементами списка
        val dividerWidth = (parent.context.resources.getDimension(R.dimen.margin_small) * parent.context.resources.displayMetrics.density).toInt()
        // Устанавливаем ширину элемента списка
        itemView.layoutParams.width =  (parent.measuredWidth - dividerWidth) / NUMBERS_OF_ITEM_TO_DISPLAY

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
            avatar.setImageResource(actor.avatarDrawable)
            name.text = actor.name
        }

    }

    companion object {
        // Число элементов отображаемых на экране
        const val NUMBERS_OF_ITEM_TO_DISPLAY  = 4
    }

}


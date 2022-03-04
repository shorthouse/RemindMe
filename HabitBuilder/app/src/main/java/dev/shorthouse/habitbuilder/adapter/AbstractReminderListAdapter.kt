package dev.shorthouse.habitbuilder.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.shorthouse.habitbuilder.databinding.ListItemReminderBinding
import dev.shorthouse.habitbuilder.model.Reminder

abstract class AbstractReminderListAdapter(
    private val clickListener: (Reminder) -> Unit
) : ListAdapter<Reminder, AbstractReminderListAdapter.ReminderViewHolder>(DiffCallback) {

    class ReminderViewHolder(
        private var binding: ListItemReminderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(reminder: Reminder) {
            binding.reminder = reminder
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Reminder>() {
        override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
            return oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = getItem(position)
        holder.itemView.setOnClickListener {
            clickListener(reminder)
        }
        holder.bind(reminder)
    }

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder
}

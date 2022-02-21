package dev.shorthouse.habitbuilder.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.shorthouse.habitbuilder.databinding.ListItemReminderBinding
import dev.shorthouse.habitbuilder.model.Reminder

class ReminderListAdapter(
    private val clickListener: (Reminder) -> Unit
) : ListAdapter<Reminder, ReminderListAdapter.ReminderViewHolder>(DiffCallback) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ReminderViewHolder(
            ListItemReminderBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = getItem(position)
        holder.itemView.setOnClickListener {
            clickListener(reminder)
        }
        holder.bind(reminder)
    }
}
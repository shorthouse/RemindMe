package dev.shorthouse.habitbuilder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.shorthouse.habitbuilder.databinding.ListItemActiveReminderBinding
import dev.shorthouse.habitbuilder.databinding.ListItemReminderBinding
import dev.shorthouse.habitbuilder.model.Reminder

class ActiveReminderListAdapter(
    private val clickListener: ClickListener)
    : ListAdapter<Reminder, ActiveReminderListAdapter.ReminderViewHolder>(DiffCallback)
{
    class ReminderViewHolder(
        private var binding: ListItemActiveReminderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(reminder: Reminder, clickListener: ClickListener) {
            binding.reminder = reminder
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    class ClickListener(
        val clickListener: (reminder: Reminder, itemId: Int) -> Unit
    )
    {
        fun onClick(reminder: Reminder, itemId: Int) = clickListener(reminder, itemId)
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
            ListItemActiveReminderBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
}
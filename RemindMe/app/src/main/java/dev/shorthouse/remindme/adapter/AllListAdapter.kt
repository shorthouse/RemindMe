package dev.shorthouse.remindme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.databinding.ListItemAllReminderBinding
import dev.shorthouse.remindme.fragments.ListContainerFragmentDirections
import dev.shorthouse.remindme.model.Reminder

class AllReminderListAdapter :
    ListAdapter<Reminder, AllReminderListAdapter.ViewHolder>(AllReminderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.list_item_all_reminder,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private var binding: ListItemAllReminderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                setDetailsClickListener { view ->
                    reminder?.let { reminder ->
                        navigateToReminderDetails(reminder.id, view)
                    }
                }
            }
        }

        private fun navigateToReminderDetails(reminderId: Long, view: View) {
            val action = ListContainerFragmentDirections
                .actionListContainerToDetails(reminderId)
            view.findNavController().navigate(action)
        }

        fun bind(item: Reminder) {
            binding.apply {
                reminder = item
                executePendingBindings()
            }
        }
    }
}

private class AllReminderDiffCallback : DiffUtil.ItemCallback<Reminder>() {

    override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem == newItem
    }
}

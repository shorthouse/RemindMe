package dev.shorthouse.remindme.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.source.local.ReminderDatabase
import dev.shorthouse.remindme.data.source.local.ReminderLocalDataSource
import dev.shorthouse.remindme.databinding.ListItemActiveReminderBinding
import dev.shorthouse.remindme.fragments.ReminderListFragmentDirections
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.viewmodel.ActiveReminderAdapterViewModel

class ActiveReminderListAdapter :
    ListAdapter<Reminder, ActiveReminderListAdapter.ViewHolder>(ActiveReminderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.list_item_active_reminder,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private var binding: ListItemActiveReminderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                setDetailsClickListener { view ->
                    viewModel?.reminder?.let { reminder ->
                        navigateToReminderDetails(reminder.id, view)
                    }
                }

                setDoneClickListener { view ->
                    viewModel?.reminder?.let {
                        cancelDisplayedReminderNotification(view.context, viewModel?.reminder?.id)
                    }
                    updateDoneReminder()
                }
            }
        }

        private fun navigateToReminderDetails(reminderId: Long, view: View) {
            val action = ReminderListFragmentDirections
                .actionActiveRemindersToReminderDetails(reminderId)
            view.findNavController().navigate(action)
        }

        private fun cancelDisplayedReminderNotification(context: Context, reminderId: Long?) {
            reminderId?.let { NotificationManagerCompat.from(context).cancel(it.toInt()) }
        }

        private fun updateDoneReminder() {
            binding.viewModel?.updateDoneReminder()
        }

        fun bind(reminder: Reminder) {
            binding.apply {
                viewModel = ActiveReminderAdapterViewModel(
                    reminder,
                    ReminderRepository(
                        ReminderLocalDataSource(
                            ReminderDatabase.getDatabase(this@ViewHolder.itemView.context)
                                .reminderDao()
                        )

                    )
                )
                executePendingBindings()
            }
        }
    }
}

private class ActiveReminderDiffCallback : DiffUtil.ItemCallback<Reminder>() {

    override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem == newItem
    }
}

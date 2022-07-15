package dev.shorthouse.remindme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.databinding.ListItemActiveReminderBinding
import dev.shorthouse.remindme.fragments.ReminderListViewPagerFragmentDirections
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.viewmodel.ActiveReminderListViewModel

class ActiveReminderListAdapter(private val viewModel: ActiveReminderListViewModel) :
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

    inner class ViewHolder(
        private val binding: ListItemActiveReminderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                setDetailsClickListener { view ->
                    navigateToReminderDetails(view, reminder)
                }

                setDoneClickListener { view ->
                    cancelDisplayedReminderNotification(view, reminder)
                    updateDoneReminder(view)
                }
            }
        }

        private fun updateDoneReminder(view: View) {
            binding.reminder?.let { reminder ->
                viewModel.updateDoneReminder(reminder)
                showUndoSnackbar(view, reminder)
            }
        }

        private fun navigateToReminderDetails(view: View, reminder: Reminder?) {
            reminder?.id?.let { reminderId ->
                val action = ReminderListViewPagerFragmentDirections
                    .actionReminderListViewPagerToReminderDetails(reminderId)
                view.findNavController().navigate(action)
            }
        }

        private fun cancelDisplayedReminderNotification(view: View, reminder: Reminder?) {
            reminder?.id?.let { reminderId ->
                NotificationManagerCompat.from(view.context).cancel(reminderId.toInt())
            }
        }

        private fun showUndoSnackbar(view: View, reminder: Reminder) {
            val context = view.context
            val reminderCompletedText = context.getString(R.string.snackbar_reminder_completed)
            val undoActionText = context.getString(R.string.snackbar_reminder_completed_undo)

            Snackbar.make(view, reminderCompletedText, Snackbar.LENGTH_SHORT)
                .setAction(undoActionText) {
                    viewModel.undoDoneReminder(reminder)
                }
                .setAnchorView(view.rootView.findViewById(R.id.add_reminder_fab))
                .show()
        }

        fun bind(reminder: Reminder) {
            binding.reminder = reminder
            binding.executePendingBindings()
        }
    }
}

private class ActiveReminderDiffCallback : DiffUtil.ItemCallback<Reminder>() {
    override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder) = oldItem == newItem
}

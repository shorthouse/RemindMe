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
import dev.shorthouse.remindme.fragments.ListContainerFragmentDirections
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.viewmodel.ActiveListViewModel

class ActiveListAdapter(private val viewModel: ActiveListViewModel) :
    ListAdapter<Reminder, ActiveListAdapter.ViewHolder>(ActiveReminderDiffCallback()) {

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

    inner class ViewHolder(private val binding: ListItemActiveReminderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Reminder) {
            binding.apply {
                binding.reminder = item
                repeatIcon.visibility = if (item.isRepeatReminder()) View.VISIBLE else View.GONE
                notificationIcon.visibility = if (item.isNotificationSent) View.VISIBLE else View.GONE
                doneCheckbox.isChecked = false

                setDetailsClickListener { view ->
                    navigateToReminderDetails(item.id, view)
                }

                setDoneClickListener { view ->
                    doneCheckbox.jumpDrawablesToCurrentState()
                    cancelDisplayedReminderNotification(item.id, view)
                    updateDoneReminder(view)
                }

                executePendingBindings()
            }
        }

        private fun updateDoneReminder(view: View) {
            binding.reminder?.let { reminder ->
                viewModel.updateDoneReminder(reminder)
                showUndoSnackbar(view, reminder)
            }
        }

        private fun navigateToReminderDetails(reminderId: Long, view: View) {
            val action = ListContainerFragmentDirections.actionListContainerToDetails(reminderId)
            view.findNavController().navigate(action)
        }

        private fun cancelDisplayedReminderNotification(reminderId: Long, view: View) {
            NotificationManagerCompat.from(view.context).cancel(reminderId.toInt())
        }

        private fun showUndoSnackbar(view: View, reminder: Reminder) {
            val context = view.context
            val reminderCompletedText = context.getString(R.string.snackbar_reminder_completed)
            val undoActionText = context.getString(R.string.snackbar_reminder_completed_undo)

            Snackbar.make(view, reminderCompletedText, Snackbar.LENGTH_SHORT)
                .setAction(undoActionText) {
                    binding.doneCheckbox.isChecked = false
                    viewModel.undoDoneReminder(reminder)
                }
                .setAnchorView(view.rootView.findViewById(R.id.add_reminder_fab))
                .show()
        }
    }
}

private class ActiveReminderDiffCallback : DiffUtil.ItemCallback<Reminder>() {
    override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder) = oldItem == newItem
}

package dev.shorthouse.remindme.fragments

import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R

@AndroidEntryPoint
class EditFragment : AddEditFragment() {
    private val navigationArgs: EditFragmentArgs by navArgs()

    override fun setupToolbar() {
        super.setupToolbar()

        binding.toolbar.title = getString(R.string.toolbar_title_edit_reminder)
    }

    override fun populateReminderData() {
        viewModel.getReminder(navigationArgs.id).observe(viewLifecycleOwner) { reminder ->
            binding.apply {
                nameInput.setText(reminder.name)
                startDateInput.setText(viewModel.getFormattedDate(reminder.startDateTime))
                startTimeInput.setText(viewModel.getFormattedTime(reminder.startDateTime))
                notesInput.setText(viewModel.getReminderNotes(reminder))
                notificationSwitch.isChecked = reminder.isNotificationSent

                if (reminder.repeatInterval != null) {
                    repeatSwitch.isChecked = true
                    repeatValueInput.setText(viewModel.getRepeatValue(reminder.repeatInterval))
                    repeatUnitRadioGroup.check(viewModel.getRadioIdFromRepeatUnit(reminder.repeatInterval))
                }
            }
        }
    }

    override fun saveReminder() {
        val editedReminder = getReminderFromInputData()
        editedReminder.id = navigationArgs.id
        viewModel.editReminder(editedReminder)
    }
}

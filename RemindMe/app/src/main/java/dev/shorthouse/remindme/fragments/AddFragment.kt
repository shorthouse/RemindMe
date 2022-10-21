package dev.shorthouse.remindme.fragments

import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.utilities.showKeyboard
import java.time.ZonedDateTime

@AndroidEntryPoint
class AddFragment : AddEditFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        focusKeyboardOnReminderName()
    }

    override fun setupToolbar() {
        super.setupToolbar()

        binding.toolbar.title = getString(R.string.toolbar_title_add_reminder)
    }

    override fun populateReminderData() {
        binding.apply {
            startDateInput.setText(viewModel.getFormattedDate(ZonedDateTime.now()))
            startTimeInput.setText(viewModel.getFormattedTimeNextHour(ZonedDateTime.now()))
        }
    }

    override fun saveReminder() {
        val newReminder = getReminderFromInputData()
        viewModel.addReminder(newReminder)
    }

    private fun focusKeyboardOnReminderName() {
        if (binding.nameInput.requestFocus()) {
            showKeyboard()
        }
    }
}

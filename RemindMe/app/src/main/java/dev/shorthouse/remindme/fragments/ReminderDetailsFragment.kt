package dev.shorthouse.remindme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.databinding.FragmentReminderDetailsBinding
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.viewmodel.ReminderDetailsViewModel

@AndroidEntryPoint
class ReminderDetailsFragment : Fragment() {
    private lateinit var binding: FragmentReminderDetailsBinding
    private val navigationArgs: ReminderDetailsFragmentArgs by navArgs()

    private val viewModel: ReminderDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReminderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getReminder(navigationArgs.id)
        setupToolbar()
        setupClickListeners()
    }

    private fun getReminder(reminderId: Long) {
        val reminderLiveData = viewModel.getReminder(reminderId)

        reminderLiveData.observe(viewLifecycleOwner) { reminder ->
            reminderLiveData.removeObservers(viewLifecycleOwner)
            viewModel.reminder = reminder
            binding.reminder = reminder
            populateData(reminder)
        }
    }

    private fun populateData(reminder: Reminder) {
        binding.apply {
            name.text = reminder.name
            startDate.text = viewModel.getFormattedStartDate(reminder)
            startTime.text = viewModel.getFormattedStartTime(reminder)
            notes.text = reminder.notes

            reminder.repeatInterval?.let {
                repeatInterval.text = resources.getQuantityString(
                    viewModel.getRepeatIntervalId(it),
                    it.timeValue.toInt()
                )
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            setupWithNavController(findNavController())

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_delete -> {
                        deleteReminder()
                        cancelReminderNotification()
                        displayToast(R.string.toast_reminder_deleted)
                        findNavController().navigateUp()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.editReminderFab.setOnClickListener {
            navigateToEditReminder()
        }
    }

    private fun navigateToEditReminder() {
        val action = ReminderDetailsFragmentDirections
            .actionReminderDetailsToAddEditReminder(
                navigationArgs.id,
                isEditReminder = true
            )
        findNavController().navigate(action)
    }

    private fun deleteReminder() {
        viewModel.deleteReminder()
    }

    private fun cancelReminderNotification() {
        if (viewModel.reminder.isNotificationSent) {
            viewModel.cancelReminderNotification()
        }
    }

    private fun displayToast(stringResId: Int) {
        Toast.makeText(
            context,
            getString(stringResId),
            Toast.LENGTH_SHORT
        ).show()
    }
}
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
                    it.timeValue.toInt(),
                    it.timeValue
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
                        getDeleteAlertDialog().show()
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

    private fun getDeleteAlertDialog(): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.alert_dialog_delete_reminder_message))
            .setNegativeButton(getString(R.string.alert_dialog_delete_reminder_cancel)) { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton(getString(R.string.alert_dialog_delete_reminder_delete)) { dialog, _ ->
                deleteReminder()
                cancelReminderNotification()
                displayToast(R.string.toast_reminder_deleted)
                dialog.dismiss()
                findNavController().navigateUp()
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
package dev.shorthouse.remindme.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dev.shorthouse.remindme.BaseApplication
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.databinding.FragmentReminderDetailsBinding
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.AlarmHelper
import dev.shorthouse.remindme.utilities.DATE_PATTERN
import dev.shorthouse.remindme.viewmodel.ReminderDetailsViewModel
import dev.shorthouse.remindme.viewmodel.ReminderDetailsViewModelFactory
import java.time.format.DateTimeFormatter

class ReminderDetailsFragment : Fragment() {
    private lateinit var binding: FragmentReminderDetailsBinding
    private val navigationArgs: ReminderDetailsFragmentArgs by navArgs()
    private lateinit var reminder: Reminder

    private val viewModel: ReminderDetailsViewModel by activityViewModels {
        ReminderDetailsViewModelFactory(
            activity?.application as BaseApplication,
            (activity?.application as BaseApplication).database.reminderDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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

        val id = navigationArgs.id
        viewModel.getReminder(id).observe(this.viewLifecycleOwner) {
            reminder = it
            binding.reminder = reminder

            binding.startDate.text = reminder.startDateTime
                .toLocalDate()
                .format(DateTimeFormatter.ofPattern(DATE_PATTERN))
                .toString()
        }

        binding.apply {
            editReminderFab.setOnClickListener {
                val action = ReminderDetailsFragmentDirections
                    .actionReminderDetailsToAddEditReminder(navigationArgs.id)
                findNavController().navigate(action)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_reminder_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                deleteReminder()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteReminder() {
        viewModel.deleteReminder(reminder)
        if (reminder.isNotificationSent) cancelNotificationAlarm(reminder)
        findNavController().navigateUp()
        Toast.makeText(
            context,
            getString(R.string.toast_reminder_deleted),
            Toast.LENGTH_SHORT
        )
            .show()
    }

    private fun cancelNotificationAlarm(reminder: Reminder) {
        AlarmHelper().cancelAlarm(requireContext(), reminder)
    }
}
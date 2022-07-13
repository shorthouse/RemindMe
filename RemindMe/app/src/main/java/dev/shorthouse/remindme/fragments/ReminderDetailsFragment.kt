package dev.shorthouse.remindme.fragments

import android.os.Bundle
import android.view.*
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
    private lateinit var reminder: Reminder

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

        val id = navigationArgs.id
        viewModel.getReminder(id).observe(this.viewLifecycleOwner) {
            reminder = it
            binding.reminder = it
        }

        binding.apply {
            viewmodel = viewModel

            editReminderFab.setOnClickListener {
                val action = ReminderDetailsFragmentDirections
                    .actionReminderDetailsToAddEditReminder(
                        navigationArgs.id,
                        isEditReminder = true
                    )
                findNavController().navigate(action)
            }
        }

        setupToolbar()
    }

    private fun setupToolbar() {
        binding.apply {
            toolbar.setupWithNavController(findNavController())

            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_delete -> {
                    deleteReminder()
                    true
                }
                else -> false
            }
        }
    }

    private fun deleteReminder() {
        viewModel.deleteReminder(reminder)

        if (reminder.isNotificationSent) cancelReminderNotification(reminder)

        Toast.makeText(
            context,
            getString(R.string.toast_reminder_deleted),
            Toast.LENGTH_SHORT
        ).show()

        findNavController().navigateUp()
    }

    private fun cancelReminderNotification(reminder: Reminder) {
        viewModel.cancelReminderNotification(reminder)
    }
}

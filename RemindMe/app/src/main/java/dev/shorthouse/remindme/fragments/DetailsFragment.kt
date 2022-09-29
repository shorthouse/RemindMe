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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.databinding.FragmentDetailsBinding
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.viewmodel.DetailsViewModel

@AndroidEntryPoint
class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    private val navigationArgs: DetailsFragmentArgs by navArgs()

    private val viewModel: DetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.transition_duration_medium).toLong()
            excludeTarget(R.id.app_bar, true)
        }

        returnTransition =  MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.transition_duration_medium).toLong()
            excludeTarget(R.id.app_bar, true)
        }

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.transition_duration_medium).toLong()
            excludeTarget(R.id.app_bar, true)
        }

        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.transition_duration_medium).toLong()
            excludeTarget(R.id.app_bar, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getReminder(navigationArgs.id)
        setupToolbar()
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
            startTime.text = reminder.getFormattedStartTime()
            notes.text = reminder.notes

            reminder.repeatInterval?.let {
                repeatInterval.text = resources.getQuantityString(
                    viewModel.getRepeatIntervalStringId(it),
                    it.timeValue.toInt(),
                    it.timeValue
                )
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            setNavigationOnClickListener { findNavController().navigateUp() }
            setNavigationIcon(R.drawable.ic_back)

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> {
                        navigateToEditReminder()
                        true
                    }
                    R.id.action_delete -> {
                        getDeleteAlertDialog().show()
                        true
                    }
                    R.id.action_complete -> {
                        getCompleteAlertDialog().show()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun getCompleteAlertDialog(): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(
            requireContext(),
            R.style.Theme_RemindMe_MaterialComponents_MaterialAlertDialog
        )
            .setTitle(getString(R.string.alert_dialog_complete_reminder_message))
            .setNegativeButton(getString(R.string.alert_dialog_delete_reminder_cancel)) { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton(getString(R.string.alert_dialog_complete_reminder_complete)) { dialog, _ ->
                viewModel.completeReminder()
                displayToast(R.string.toast_reminder_completed)
                dialog.dismiss()
                findNavController().navigateUp()
            }
    }

    private fun getDeleteAlertDialog(): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(
            requireContext(),
            R.style.Theme_RemindMe_MaterialComponents_MaterialAlertDialog
        )
            .setTitle(getString(R.string.alert_dialog_delete_reminder_message))
            .setNegativeButton(getString(R.string.alert_dialog_delete_reminder_cancel)) { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton(getString(R.string.alert_dialog_delete_reminder_delete)) { dialog, _ ->
                viewModel.deleteReminder()
                displayToast(R.string.toast_reminder_deleted)
                dialog.dismiss()
                findNavController().navigateUp()
            }
    }

    private fun navigateToEditReminder() {
        val action = DetailsFragmentDirections
            .actionDetailsToAddEdit(
                navigationArgs.id,
                isEditReminder = true
            )

        findNavController().navigate(action)
    }

    private fun displayToast(stringResId: Int) {
        Toast.makeText(context, getString(stringResId), Toast.LENGTH_SHORT)
            .show()
    }
}
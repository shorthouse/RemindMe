package dev.shorthouse.remindme.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.databinding.FragmentAddEditReminderBinding
import dev.shorthouse.remindme.viewmodel.AddEditReminderViewModel

@AndroidEntryPoint
class AddEditReminderFragment : Fragment() {
    private lateinit var binding: FragmentAddEditReminderBinding
    private val navigationArgs: AddEditReminderFragmentArgs by navArgs()
    private val viewModel: AddEditReminderViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddEditReminderBinding.inflate(inflater, container, false)
            .apply {
                viewmodel = viewModel

                startDateInput.setOnClickListener { displayDatePicker() }
                startTimeInput.setOnClickListener { displayTimePicker() }
                intervalTimeValueInput.doAfterTextChanged { updateRepeatIntervalDropdown(it) }
            }
        setHasOptionsMenu(true) //TODO needed?
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (navigationArgs.isEditReminder) {
            viewModel.getReminder(navigationArgs.id)
                .observe(this.viewLifecycleOwner) { reminder ->
                    binding.reminder = reminder
                    binding.repeatSwitch.isChecked =
                        viewModel.getIsRepeatChecked(binding.reminder)
                    binding.notificationSwitch.isChecked = reminder.isNotificationSent
                }
        }

        setupTopAppBar()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_add_edit_reminder, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupTopAppBar() {
        binding.topAppBar.setupWithNavController(findNavController())

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.saveReminder.setOnClickListener {
            if (isReminderValid()) {
                saveReminder()
                hideKeyboard()
                displayToast(R.string.toast_reminder_saved)
                findNavController().popBackStack()
            }
        }
    }

    private fun saveReminder() {
        val reminderName = binding.nameInput.text.toString()

        val reminderStartDateTime = viewModel.convertDateTimeStringToDateTime(
            binding.startDateInput.text.toString(),
            binding.startTimeInput.text.toString()
        )

        val repeatInterval = viewModel.getRepeatInterval(
            binding.repeatSwitch.isChecked,
            binding.intervalTimeValueInput.text.toString().toLong(),
            binding.intervalTimeUnitInput.text.toString()
        )

        val reminderNotes = viewModel.getReminderNotes(binding.notesInput.text.toString())

        val isArchived = false

        val isNotificationSent = binding.notificationSwitch.isChecked

        viewModel.saveReminder(
            navigationArgs.id,
            reminderName,
            reminderStartDateTime,
            repeatInterval,
            reminderNotes,
            isArchived,
            isNotificationSent
        )
    }

    private fun isReminderValid(): Boolean {
        val name = binding.nameInput.text.toString()
        if (!viewModel.isNameValid(name)) {
            displayToast(R.string.error_name_empty)
            return false
        }

        val startDate = binding.startDateInput.text.toString()
        val startTime = binding.startTimeInput.text.toString()
        if (!viewModel.isStartTimeValid(startDate, startTime)) {
            displayToast(R.string.error_time_past)
            return false
        }

        val repeatIntervalValue = binding.intervalTimeValueInput.text.toString().toLong()
        if (!viewModel.isRepeatIntervalValid(repeatIntervalValue)) {
            displayToast(R.string.error_interval_zero)
            return false
        }

        return true
    }

    private fun updateRepeatIntervalDropdown(timeValueEditable: Editable?) {
        if (timeValueEditable.toString().isBlank()) return
        val timeValue = timeValueEditable.toString().toLong()

        val dropdownItems = listOf(
            resources.getQuantityString(R.plurals.dropdown_days, timeValue.toInt()),
            resources.getQuantityString(R.plurals.dropdown_weeks, timeValue.toInt())
        )

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.list_item_dropdown_interval,
            dropdownItems
        )

        val timeUnitInput = binding.intervalTimeUnitInput
        when (timeUnitInput.text.toString()) {
            in getString(R.string.time_unit_days) -> timeUnitInput.setText(dropdownItems[0])
            else -> timeUnitInput.setText(dropdownItems[1])
        }

        binding.intervalTimeUnitInput.setAdapter(adapter)
    }

    private fun displayDatePicker() {
        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.title_date_picker))
            .setCalendarConstraints(constraints)
            .build()

        datePicker.addOnPositiveButtonClickListener { dateTimestamp ->
            binding.startDateInput.setText(
                viewModel.convertTimestampToDateString(dateTimestamp)
            )
        }

        datePicker.show(parentFragmentManager, getString(R.string.tag_reminder_date_picker))
    }

    private fun displayTimePicker() {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText(getString(R.string.title_time_picker))
            .build()

        timePicker.addOnPositiveButtonClickListener {
            binding.startTimeInput.setText(
                getString(
                    R.string.format_reminder_time,
                    timePicker.hour.toString().padStart(2, '0'),
                    timePicker.minute.toString().padStart(2, '0'),
                )
            )
        }

        timePicker.show(parentFragmentManager, getString(R.string.tag_reminder_time_picker))
    }

    private fun hideKeyboard() {
        val inputManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputManager.hideSoftInputFromWindow(
            view?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    private fun displayToast(stringResId: Int) {
        Toast.makeText(
            context,
            getString(stringResId),
            Toast.LENGTH_SHORT
        ).show()
    }
}

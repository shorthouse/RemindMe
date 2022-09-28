package dev.shorthouse.remindme.fragments

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.databinding.FragmentAddEditBinding
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.SpinnerArrayAdapter
import dev.shorthouse.remindme.utilities.setOnClickThrottleListener
import dev.shorthouse.remindme.viewmodel.AddEditViewModel
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@AndroidEntryPoint
class AddEditFragment : Fragment() {
    private lateinit var binding: FragmentAddEditBinding
    private val navigationArgs: AddEditFragmentArgs by navArgs()
    private val viewModel: AddEditViewModel by viewModels()

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
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAddOrEdit()
        setupToolbar()
        populateData()
        setupClickListeners()

        if (viewModel.isAddReminder) {
            focusKeyboardOnReminderName()
        }
    }

    private fun setAddOrEdit() {
        viewModel.isEditReminder = navigationArgs.isEditReminder
        viewModel.isAddReminder = viewModel.isEditReminder.not()
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            setNavigationOnClickListener {
                hideKeyboard()
                findNavController().navigateUp()
            }

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_save -> {
                        if (isReminderValid()) {
                            saveReminder()
                            hideKeyboard()
                            displayToast(R.string.toast_reminder_saved)
                            findNavController().navigateUp()
                        }
                        true
                    }
                    else -> false
                }
            }

            title = if (viewModel.isAddReminder) {
                getString(R.string.toolbar_title_add_reminder)
            } else {
                getString(R.string.toolbar_title_edit_reminder)
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            startDateInput.setOnClickThrottleListener {
                displayDatePicker()
            }

            startTimeInput.setOnClickThrottleListener {
                displayTimePicker()
            }

            repeatValueInput.doAfterTextChanged {
                val dropdownRepeatUnit =
                    viewModel.repeatPeriodChronoUnitMap[repeatUnitInput.text.toString()]
                dropdownRepeatUnit?.let { setDropdownList(it) }
            }
        }
    }

    private fun populateData() {
        if (viewModel.isAddReminder) {
            populateAddData()
        } else {
            viewModel.getReminder(navigationArgs.id).observe(viewLifecycleOwner) { reminder ->
                populateEditData(reminder)
            }
        }
    }

    private fun populateAddData() {
        binding.apply {
            startDateInput.setText(viewModel.getFormattedDate(ZonedDateTime.now()))
            startTimeInput.setText(viewModel.getFormattedTimeNextHour(ZonedDateTime.now()))
            repeatValueInput.setText(viewModel.defaultRepeatValue)
            setDropdownList(viewModel.defaultRepeatUnit)
        }
    }

    private fun populateEditData(reminder: Reminder) {
        binding.apply {
            nameInput.setText(reminder.name)
            startDateInput.setText(viewModel.getFormattedDate(reminder.startDateTime))
            startTimeInput.setText(viewModel.getFormattedTime(reminder.startDateTime))
            notesInput.setText(viewModel.getReminderNotes(reminder))
            notificationSwitch.isChecked = reminder.isNotificationSent
            repeatSwitch.isChecked = reminder.isRepeatReminder()
            repeatValueInput.setText(viewModel.getRepeatValue(reminder))
            setDropdownList(viewModel.getRepeatUnit(reminder))
        }
    }

    private fun setDropdownList(repeatUnit: ChronoUnit) {
        val repeatValue = binding.repeatValueInput.text.toString().toIntOrNull() ?: 0

        val dropdownItems = listOf(
            resources.getQuantityString(R.plurals.dropdown_days, repeatValue),
            resources.getQuantityString(R.plurals.dropdown_weeks, repeatValue)
        )

        val spinnerAdapter = SpinnerArrayAdapter(requireContext(), dropdownItems)
        binding.repeatUnitInput.setAdapter(spinnerAdapter)

        val selectedItem = when (repeatUnit) {
            ChronoUnit.DAYS -> dropdownItems[0]
            else -> dropdownItems[1]
        }

        binding.repeatUnitInput.setText(selectedItem, false)
    }

    private fun saveReminder() {
        binding.apply {
            val reminderName = nameInput.text.toString()

            val reminderStartDateTime = viewModel.convertDateTimeStringToDateTime(
                startDateInput.text.toString(),
                startTimeInput.text.toString()
            )

            val repeatInterval = if (binding.repeatSwitch.isChecked) {
                 viewModel.getRepeatInterval(
                    repeatValueInput.text.toString().toLong(),
                    repeatUnitInput.text.toString()
                )
            } else {
                null
            }

            val reminderNotes = viewModel.getReminderNotes(notesInput.text.toString())

            val isArchived = false

            val isNotificationSent = notificationSwitch.isChecked

            if (viewModel.isAddReminder) {
                viewModel.addReminder(
                    reminderName,
                    reminderStartDateTime,
                    repeatInterval,
                    reminderNotes,
                    isArchived,
                    isNotificationSent
                )
            } else if (viewModel.isEditReminder) {
                viewModel.editReminder(
                    navigationArgs.id,
                    reminderName,
                    reminderStartDateTime,
                    repeatInterval,
                    reminderNotes,
                    isArchived,
                    isNotificationSent
                )
            }
        }
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

        val repeatIntervalValue = binding.repeatValueInput.text.toString().toLongOrNull() ?: 0L
        if (viewModel.isRepeatIntervalEmpty(repeatIntervalValue)) {
            displayToast(R.string.error_interval_empty)
            return false
        }

        return true
    }

    private fun displayDatePicker() {
        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(constraints)
            .setTitleText("")
            .build()

        datePicker.addOnPositiveButtonClickListener { dateTimestamp ->
            binding.startDateInput.setText(
                viewModel.convertEpochMilliToDate(dateTimestamp)
            )
        }

        datePicker.show(parentFragmentManager, getString(R.string.tag_reminder_date_picker))
    }

    private fun displayTimePicker() {
        val onTimeSetListener = TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
            binding.startTimeInput.setText(
                viewModel.formatTimePickerTime(selectedHour, selectedMinute)
            )
        }

        val timePickerDialog = TimePickerDialog(context, onTimeSetListener, 0, 0, true)

        timePickerDialog.show()
    }

    private fun showKeyboard() {
        val inputManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputManager.showSoftInput(binding.nameInput, SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val inputManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputManager.hideSoftInputFromWindow(
            view?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    private fun focusKeyboardOnReminderName() {
        if (binding.nameInput.requestFocus()) {
            showKeyboard()
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
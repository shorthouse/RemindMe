package dev.shorthouse.remindme.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.databinding.FragmentAddEditReminderBinding
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.viewmodel.AddEditReminderViewModel
import java.time.temporal.ChronoUnit

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAddOrEdit()
        setupToolbar()
        setupClickListeners()
        populateData()
        if (viewModel.isAddReminder) {
            focusKeyboardOnReminderName()
        }
    }

    private fun setAddOrEdit() {
        viewModel.isEditReminder = navigationArgs.isEditReminder
    }

    private fun setupToolbar() {
        binding.apply {
            toolbar.setupWithNavController(findNavController())

            toolbar.setNavigationIcon(R.drawable.ic_close)

            toolbar.title = when (navigationArgs.isEditReminder) {
                true -> getString(R.string.toolbar_title_edit_reminder)
                else -> getString(R.string.toolbar_title_add_reminder)
            }

            saveReminder.setOnClickListener {
                if (isReminderValid()) {
                    saveReminder()
                    hideKeyboard()
                    displayToast(R.string.toast_reminder_saved)
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            startDateInput.setOnClickListener {
                displayDatePicker()
            }

            startTimeInput.setOnClickListener {
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
        if (viewModel.isEditReminder) {
            viewModel.getReminder(navigationArgs.id).observe(viewLifecycleOwner) { reminder ->
                populateEditData(reminder)
            }
        } else {
            populateAddData()
        }
    }

    private fun populateEditData(reminder: Reminder) {
        binding.apply {
            nameInput.setText(reminder.name)
            startDateInput.setText(viewModel.formatReminderStartDate(reminder))
            startTimeInput.setText(viewModel.formatReminderStartTime(reminder))
            notesInput.setText(viewModel.getReminderNotes(reminder))
            notificationSwitch.isChecked = reminder.isNotificationSent
            repeatSwitch.isChecked = reminder.isRepeatReminder()
            repeatValueInput.setText(viewModel.getRepeatValue(reminder))
            setDropdownList(viewModel.getRepeatUnit(reminder))
        }
    }

    private fun populateAddData() {
        binding.apply {
            startDateInput.setText(viewModel.getStartDateNow())
            startTimeInput.setText(viewModel.getStartTimeNextHour())
            repeatValueInput.setText(viewModel.defaultRepeatValue)
            setDropdownList(viewModel.defaultRepeatUnit)
        }
    }

    private fun setDropdownList(repeatUnit: ChronoUnit) {
        val repeatValue = binding.repeatValueInput.text.toString().toInt()

        val dropdownItems = listOf(
            resources.getQuantityString(R.plurals.dropdown_days, repeatValue),
            resources.getQuantityString(R.plurals.dropdown_weeks, repeatValue)
        )

        binding.repeatUnitInput.setAdapter(
            ArrayAdapter(
                requireContext(),
                R.layout.list_item_dropdown_interval,
                dropdownItems
            )
        )

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

            val repeatInterval = viewModel.getRepeatInterval(
                repeatValueInput.text.toString().toLong(),
                repeatUnitInput.text.toString()
            )

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

        val repeatIntervalValue = binding.repeatValueInput.text.toString().toLong()
        if (!viewModel.isRepeatIntervalValid(repeatIntervalValue)) {
            displayToast(R.string.error_interval_zero)
            return false
        }

        return true
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
                viewModel.formatDatePickerDate(dateTimestamp)
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
                viewModel.formatTimePickerTime(timePicker.hour, timePicker.minute)
            )
        }

        timePicker.show(parentFragmentManager, getString(R.string.tag_reminder_time_picker))
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

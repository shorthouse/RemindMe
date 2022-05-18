package dev.shorthouse.remindme.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dev.shorthouse.remindme.BaseApplication
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.databinding.FragmentAddEditReminderBinding
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.AlarmHelper
import dev.shorthouse.remindme.viewmodel.AddEditReminderViewModelFactory
import dev.shorthouse.remindme.viewmodel.AddReminderViewModel

class AddEditReminderFragment : Fragment() {

    private val navigationArgs: AddEditReminderFragmentArgs by navArgs()

    private lateinit var binding: FragmentAddEditReminderBinding

    private val viewModel: AddReminderViewModel by activityViewModels {
        AddEditReminderViewModelFactory(
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
        binding = FragmentAddEditReminderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (navigationArgs.isEditReminder) {
            viewModel.getReminder(navigationArgs.id).observe(this.viewLifecycleOwner) {
                binding.reminder = it
                binding.repeatSwitch.isChecked = viewModel.getIsRepeatChecked(binding.reminder)
                binding.notificationSwitch.isChecked = it.isNotificationSent
            }
        }

        binding.apply {
            addReminderFragment = this@AddEditReminderFragment
            viewmodel = viewModel

            intervalTimeValueInput.doAfterTextChanged {
                if (it.toString().isNotBlank()) setDropdownTimeUnitAdapter(it.toString())
            }
        }

        viewModel.newReminder.observe(viewLifecycleOwner) { reminder ->
            reminder?.let {
                updateNotificationAlarms(reminder)
                viewModel.clearLiveData()
                navigateUp()
            }
        }
    }

    private fun setDropdownTimeUnitAdapter(timeValueString: String) {
        val timeValue = timeValueString.toLong()

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_add_edit_reminder, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_done).actionView
            .findViewById<MaterialButton>(R.id.save_reminder)
            .setOnClickListener {
                if (isReminderValid()) {
                    saveReminder()
                    hideKeyboard()
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

        if (navigationArgs.isEditReminder) {
            viewModel.updateReminder(
                navigationArgs.id,
                reminderName,
                reminderStartDateTime,
                repeatInterval,
                reminderNotes,
                isArchived,
                isNotificationSent
            )
        } else {
            viewModel.addReminder(
                reminderName,
                reminderStartDateTime,
                repeatInterval,
                reminderNotes,
                isArchived,
                isNotificationSent
            )
        }
    }

    private fun updateNotificationAlarms(reminder: Reminder) {
        if (navigationArgs.isEditReminder) cancelExistingAlarmNotification(reminder)
        if (reminder.isNotificationSent) scheduleAlarmNotification(reminder)
    }

    private fun scheduleAlarmNotification(reminder: Reminder) {
        AlarmHelper().setNotificationAlarm(requireContext(), reminder)
    }

    private fun cancelExistingAlarmNotification(reminder: Reminder) {
        AlarmHelper().cancelExistingNotificationAlarm(requireContext(), reminder)
    }

    fun displayDatePicker() {
        val constraints =
            CalendarConstraints.Builder()
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

    fun displayTimePicker() {
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

    private fun navigateUp() {
        Toast.makeText(
            context,
            getString(R.string.toast_reminder_saved),
            Toast.LENGTH_SHORT
        ).show()
        findNavController().navigateUp()
    }

    private fun isReminderValid(): Boolean {
        val name = binding.nameInput.text.toString()
        if (!viewModel.isNameValid(name)) {
            makeShortToast(R.string.error_name_empty)
            return false
        }

        val startDate = binding.startDateInput.text.toString()
        val startTime = binding.startTimeInput.text.toString()
        if (!viewModel.isStartTimeValid(startDate, startTime)) {
            makeShortToast(R.string.error_time_past)
            return false
        }

        val repeatIntervalValue = binding.intervalTimeValueInput.text.toString().toLong()
        if (!viewModel.isRepeatIntervalValid(repeatIntervalValue)) {
            makeShortToast(R.string.error_interval_zero)
            return false
        }

        return true
    }

    private fun makeShortToast(stringResId: Int) {
        Toast.makeText(
            context,
            getString(stringResId),
            Toast.LENGTH_SHORT
        ).show()
    }
}
package dev.shorthouse.remindme.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
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
import dev.shorthouse.remindme.data.RepeatInterval
import dev.shorthouse.remindme.databinding.FragmentAddEditReminderBinding
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.receivers.AlarmNotificationReceiver
import dev.shorthouse.remindme.viewmodel.AddEditReminderViewModelFactory
import dev.shorthouse.remindme.viewmodel.AddReminderViewModel
import java.time.temporal.ChronoUnit

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

        val id = navigationArgs.id
        if (id > 0) {
            viewModel.getReminder(id).observe(this.viewLifecycleOwner) {
                binding.reminder = it
                binding.repeatSwitch.isChecked = viewModel.getIsRepeatChecked(binding.reminder)
                binding.notificationSwitch.isChecked = it.isNotificationSent
                //binding.intervalTimeValueInput.setText(it.repeatInterval?.timeValue.toString())
            }
        }

        binding.apply {
            addReminderFragment = this@AddEditReminderFragment
            viewmodel = viewModel

            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.list_item_dropdown_interval,
                listOf("day", "week")
            )
            dropdownIntervalMenuInput.setAdapter(adapter)
        }

        viewModel.reminder.observe(viewLifecycleOwner) { reminder ->
            reminder?.let {
                scheduleAlarmNotification(reminder)
                viewModel.clearLiveData()
                navigateUp()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_add_edit_reminder, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_done).actionView
            .findViewById<MaterialButton>(R.id.save_reminder)
            .setOnClickListener {
                if (isDetailValid()) {
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

        val repeatInterval = if (binding.repeatSwitch.isChecked) {
            val timeValueString = binding.intervalTimeValueInput.text.toString()
            val timeValueUnit = when (binding.dropdownIntervalMenuInput.text.toString()) {
                getString(R.string.repeat_interval_day) -> ChronoUnit.DAYS
                else -> ChronoUnit.WEEKS
            }
            RepeatInterval(timeValueString.toLong(), timeValueUnit)
        } else null

        val reminderNotes = if (binding.notesInput.text.isNullOrBlank()) {
            null
        } else {
            binding.notesInput.text.toString()
        }

        val isArchived = false

        val isNotificationSent = binding.notificationSwitch.isChecked

        if (navigationArgs.id > 0) {
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

    private fun scheduleAlarmNotification(reminder: Reminder) {
        val alarmManager =
            context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, AlarmNotificationReceiver::class.java)
        alarmIntent.putExtra("reminderName", reminder.name)
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                reminder.id.toInt(),
                alarmIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT,
            )

        if (reminder.repeatInterval == null) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                viewModel.getReminderStartDateTimeMillis(reminder.startDateTime),
                pendingIntent
            )
        } else {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                viewModel.getReminderStartDateTimeMillis(reminder.startDateTime),
                viewModel.getRepeatIntervalMillis(reminder.repeatInterval),
                pendingIntent
            )
        }
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

    private fun isDetailValid(): Boolean {
        val name = binding.nameInput.text.toString()
        val startDate = binding.startDateInput.text.toString()
        val startTime = binding.startTimeInput.text.toString()
        val startDateTime = viewModel.convertDateTimeStringToDateTime(startDate, startTime)

        val isDetailValid = viewModel.isDetailValid(name, startDateTime)

        return if (isDetailValid) {
            isDetailValid
        } else {
            makeShortToast(viewModel.getDetailError(name))
            isDetailValid
        }
    }

    private fun makeShortToast(stringResId: Int) {
        Toast.makeText(
            context,
            getString(stringResId),
            Toast.LENGTH_SHORT
        ).show()
    }
}
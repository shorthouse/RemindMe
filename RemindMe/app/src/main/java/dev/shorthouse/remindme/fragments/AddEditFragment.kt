package dev.shorthouse.remindme.fragments

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.transition.MaterialSharedAxis
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.databinding.FragmentAddEditBinding
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.hideKeyboard
import dev.shorthouse.remindme.utilities.setOnClickThrottleListener
import dev.shorthouse.remindme.utilities.showToast
import dev.shorthouse.remindme.viewmodel.AddEditViewModel

abstract class AddEditFragment : Fragment() {
    protected lateinit var binding: FragmentAddEditBinding

    protected val viewModel: AddEditViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransitionAnimations()
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

        setupToolbar()
        setupClickListeners()
        populateReminderData()
    }

    private fun setTransitionAnimations() {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.transition_duration_medium).toLong()
            excludeTarget(R.id.app_bar, true)
        }

        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.transition_duration_medium).toLong()
            excludeTarget(R.id.app_bar, true)
        }
    }

    protected open fun setupToolbar() {
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
                            showToast(R.string.toast_reminder_saved, requireContext())
                            findNavController().navigateUp()
                        } else {
                            showToast(getReminderInputErrorMessage(), requireContext())
                        }
                        true
                    }
                    else -> false
                }
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
                updateRepeatUnitRadioText()
            }
        }
    }

    protected fun getReminderFromInputData(): Reminder {
        binding.apply {
            return Reminder(
                name = viewModel.getReminderName(nameInput.text.toString()),
                startDateTime = viewModel.convertDateTimeStringToDateTime(
                    startDateInput.text.toString(),
                    startTimeInput.text.toString()
                ),
                repeatInterval = viewModel.getRepeatInterval(
                    binding.repeatSwitch.isChecked,
                    repeatValueInput.text.toString().toLong(),
                    viewModel.getRepeatUnitFromRadioId(repeatUnitRadioGroup.checkedRadioButtonId)
                ),
                notes = viewModel.getReminderNotes(notesInput.text.toString()),
                isArchived = false,
                isNotificationSent = notificationSwitch.isChecked
            )
        }
    }

    private fun isReminderValid(): Boolean {
        val name = binding.nameInput.text.toString()
        val startDate = binding.startDateInput.text.toString()
        val startTime = binding.startTimeInput.text.toString()
        val repeatIntervalValue = binding.repeatValueInput.text.toString().toLongOrNull() ?: 0L

        return viewModel.isNameValid(name) &&
            viewModel.isStartTimeValid(startDate, startTime) &&
            viewModel.isRepeatIntervalValid(repeatIntervalValue)
    }

    private fun getReminderInputErrorMessage(): Int {
        val name = binding.nameInput.text.toString()
        val startDate = binding.startDateInput.text.toString()
        val startTime = binding.startTimeInput.text.toString()
        val repeatIntervalValue = binding.repeatValueInput.text.toString().toLongOrNull() ?: 0L

        return when {
            !viewModel.isNameValid(name) -> R.string.error_name_empty
            !viewModel.isStartTimeValid(startDate, startTime) -> R.string.error_time_past
            !viewModel.isRepeatIntervalValid(repeatIntervalValue) -> R.string.error_interval_empty
            else -> R.string.error_input
        }
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

    private fun updateRepeatUnitRadioText() {
        binding.apply {
            val repeatValue = viewModel.getRepeatValueText(repeatValueInput.text.toString())

            val repeatUnitDaysString = resources.getQuantityString(R.plurals.radio_button_days, repeatValue)
            val repeatUnitWeeksString = resources.getQuantityString(R.plurals.radio_button_weeks, repeatValue)

            repeatUnitDays.text = repeatUnitDaysString
            repeatUnitWeeks.text = repeatUnitWeeksString
        }
    }

    abstract fun populateReminderData()

    abstract fun saveReminder()
}

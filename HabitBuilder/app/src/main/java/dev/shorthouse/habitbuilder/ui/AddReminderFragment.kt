package dev.shorthouse.habitbuilder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dev.shorthouse.habitbuilder.BaseApplication
import dev.shorthouse.habitbuilder.R
import dev.shorthouse.habitbuilder.databinding.AddReminderFragmentBinding
import dev.shorthouse.habitbuilder.ui.viewmodel.ReminderViewModel
import dev.shorthouse.habitbuilder.ui.viewmodel.ReminderViewModelFactory
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class AddReminderFragment : Fragment() {
    private var _binding: AddReminderFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReminderViewModel by activityViewModels {
        ReminderViewModelFactory(
            (activity?.application as BaseApplication).database.habitDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddReminderFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            startTimeInput.setOnClickListener {
                displayTimePicker()
                startTimeLabel.error = null
            }

            startDateInput.setOnClickListener {
                displayDatePicker()
                startDateLabel.error = null
            }

            nameInput.addTextChangedListener {
                nameLabel.error = null
            }

            saveHabit.setOnClickListener {
                addHabit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun displayDatePicker() {
        val datePicker = getDatePicker()

        datePicker.addOnPositiveButtonClickListener { dateTimestamp ->
            binding.startDateInput.setText(viewModel.getFormattedDate(dateTimestamp))
        }

        datePicker.show(parentFragmentManager, getString(R.string.reminder_date_picker_tag))
    }

    private fun getDatePicker(): MaterialDatePicker<Long> {
        return MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.date_picker_title))
            .build()
    }

    private fun displayTimePicker() {
        val timePicker = getTimePicker()

        timePicker.addOnPositiveButtonClickListener {
            binding.startTimeInput.setText(
                getString(
                    R.string.reminder_time,
                    timePicker.hour.toString().padStart(2, '0'),
                    timePicker.minute.toString().padStart(2, '0'),
                ))
        }

        timePicker.show(parentFragmentManager, getString(R.string.reminder_time_picker_tag))
    }

    private fun getTimePicker(): MaterialTimePicker {
        return MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText(getString(R.string.time_picker_title))
            .build()
    }

    private fun addHabit() {
        if(isValidEntry()) {
            val reminderEpoch = getReminderEpoch()
            val nowEpoch = Instant.now().epochSecond
            val secondsUntilReminder = reminderEpoch - nowEpoch

            viewModel.addReminder(
                binding.nameInput.text.toString(),
                reminderEpoch,
                ""
            )

            Toast.makeText(context, getString(R.string.toast_reminder_saved), Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    private fun getReminderDate(): LocalDate {
        val reminderDateText = binding.startDateInput.text.toString()
        return viewModel.getReminderDate(reminderDateText)
    }

    private fun getReminderDateTime(): LocalDateTime {
        val reminderDateText = binding.startDateInput.text.toString()
        val reminderTimeText = binding.startTimeInput.text.toString()
        return viewModel.getReminderDateTime(reminderDateText, reminderTimeText)
    }

    private fun getReminderEpoch() : Long {
        return getReminderDateTime().atZone(ZoneId.systemDefault()).toEpochSecond()
    }

    private fun isValidEntry(): Boolean {
        when {
            binding.nameInput.text.toString().isBlank() -> {
                binding.nameLabel.error = "Enter a name"
                return false;
            }
            binding.startDateInput.text.toString().isBlank() -> {
                binding.startDateLabel.error = "Enter a date"
                return false;
            }
            getReminderDate().isBefore(LocalDate.now()) -> {
                binding.startDateLabel.error = "Enter a current or future date"
                return false;
            }
            binding.startTimeInput.text.toString().isBlank() -> {
                binding.startTimeLabel.error = "Enter a time"
                return false;
            }
            getReminderDateTime().isBefore(LocalDateTime.now()) -> {
                binding.startTimeLabel.error = "Enter a current or future time"
                return false;
            }
            else -> return true
        }

    }
}
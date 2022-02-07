package dev.shorthouse.habitbuilder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dev.shorthouse.habitbuilder.BaseApplication
import dev.shorthouse.habitbuilder.R
import dev.shorthouse.habitbuilder.databinding.AddReminderFragmentBinding
import dev.shorthouse.reminderbuilder.ui.viewmodel.ReminderViewModel
import dev.shorthouse.reminderbuilder.ui.viewmodel.ReminderViewModelFactory
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*


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

        binding.startDateInput.setOnClickListener {
            displayDatePicker(getDatePicker())
        }

        binding.startTimeInput.setOnClickListener {
            displayTimePicker(getTimePicker())
        }

        binding.saveHabit.setOnClickListener{
            addHabit()
            Toast.makeText(context, "Habit saved!", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getDatePicker(): MaterialDatePicker<Long> {
        return MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .build()
    }

    private fun displayDatePicker(datePicker: MaterialDatePicker<Long>) {
        val dateFormatter = SimpleDateFormat("EEE dd MMM yyyy", Locale.getDefault())

        datePicker.addOnPositiveButtonClickListener { dateTimestamp ->
            binding.startDateInput.setText(dateFormatter.format(dateTimestamp))
        }

        datePicker.show(parentFragmentManager, "HABIT_DATE_PICKER")
    }

    private fun getTimePicker(): MaterialTimePicker {
        return MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText("Select habit start time")
            .build()
    }

    private fun displayTimePicker(timePicker: MaterialTimePicker) {
        timePicker.addOnPositiveButtonClickListener {
            binding.startTimeInput.setText(
                getString(
                    R.string.habit_time,
                    timePicker.hour.toString().padStart(2, '0'),
                    timePicker.minute.toString().padStart(2, '0'),
            ))
        }

        timePicker.show(parentFragmentManager, "HABIT_TIME_PICKER")
    }

    private fun getReminderEpoch(): Long {
        val reminderDate = binding.startDateInput.text.toString()
        val reminderTime = binding.startTimeInput.text.toString()
        val reminderDateTime = "$reminderDate $reminderTime"

        val formatter = DateTimeFormatter.ofPattern("EEE dd MMM yyyy HH:mm")
        val localDateTime = LocalDateTime.parse(reminderDateTime, formatter)

        val zoneId = ZoneId.systemDefault()
        return localDateTime.atZone(zoneId).toEpochSecond()
    }

    private fun addHabit() {
        val reminderEpoch = getReminderEpoch()
        val nowEpoch = Instant.now().epochSecond
        val secondsUntilReminder = reminderEpoch - nowEpoch

        viewModel.addReminder(
            binding.nameInput.text.toString(),
            secondsUntilReminder,
            ""
        )
    }
}
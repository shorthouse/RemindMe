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
            displayDatePicker()
        }

        binding.startTimeInput.setOnClickListener {
            displayTimePicker()
        }

        binding.saveHabit.setOnClickListener{
            val dateFormatter = SimpleDateFormat("EEE dd MMM yyyy", Locale.getDefault())
            val selectedDate = dateFormatter.parse(binding.startDateInput.text.toString())
            val dateInMs = selectedDate.time

            Toast.makeText(context, "Habit saved!", Toast.LENGTH_SHORT).show()
            viewModel.addHabit(
                binding.nameInput.text.toString(),
                dateInMs,
                ""
            )
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun displayDatePicker() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .build()

        datePicker.show(parentFragmentManager, "HABIT_DATE_PICKER")

        val dateFormatter = SimpleDateFormat("EEE dd MMM yyyy", Locale.getDefault())
        datePicker.addOnPositiveButtonClickListener { selection ->
            binding.startDateInput.setText(dateFormatter.format(selection))
        }
    }

    private fun displayTimePicker() {
        val timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText("Select habit start time")
                .build()

        timePicker.show(parentFragmentManager, "HABIT_TIME_PICKER")

        timePicker.addOnPositiveButtonClickListener {
            binding.startTimeInput.setText(getString(
                R.string.habit_time,
                timePicker.hour.toString().padStart(2, '0'),
                timePicker.minute.toString().padStart(2, '0'),
            ))
        }
    }
}
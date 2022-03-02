package dev.shorthouse.habitbuilder.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dev.shorthouse.habitbuilder.BaseApplication
import dev.shorthouse.habitbuilder.R
import dev.shorthouse.habitbuilder.databinding.FragmentAddReminderBinding
import dev.shorthouse.habitbuilder.ui.viewmodel.AddReminderViewModel
import dev.shorthouse.habitbuilder.ui.viewmodel.AddReminderViewModelFactory
import java.time.LocalDate
import java.time.LocalDateTime

class AddReminderFragment : Fragment() {
    private var _binding: FragmentAddReminderBinding? = null
    private val binding get() = _binding!!


    private val viewModel: AddReminderViewModel by activityViewModels {
        AddReminderViewModelFactory(
            (activity?.application as BaseApplication).database.reminderDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddReminderBinding.inflate(inflater, container, false)
        disableBottomNav()
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

            yearsInput.addTextChangedListener {
                yearsLabel.error = null
            }

            daysInput.addTextChangedListener {
                daysLabel.error = null
            }

            hoursInput.addTextChangedListener {
                hoursLabel.error = null
            }

            saveReminder.setOnClickListener {
                addHabit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        enableBottomNav()
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

    private fun enableBottomNav() {
        val appBar: BottomAppBar = requireActivity().findViewById(R.id.bottom_app_bar)
        val reminderFab: FloatingActionButton = requireActivity().findViewById(R.id.add_reminder_fab)
        appBar.performShow()
        reminderFab.show()
    }

    private fun disableBottomNav() {
        val appBar: BottomAppBar = requireActivity().findViewById(R.id.bottom_app_bar)
        val reminderFab: FloatingActionButton = requireActivity().findViewById(R.id.add_reminder_fab)
        appBar.performHide()
        reminderFab.hide()
    }

    private fun addHabit() {
        if(isValidEntry()) {
            val reminderStartEpoch = getReminderStartEpoch()
            val secondsUntilReminder = viewModel.getSecondsUntilReminder(reminderStartEpoch)
            val reminderInterval = viewModel.getReminderInterval(
                binding.yearsInput.text.toString().toLong(),
                binding.daysInput.text.toString().toLong(),
                binding.hoursInput.text.toString().toLong(),
            )

            viewModel.addReminder(
                binding.nameInput.text.toString(),
                reminderStartEpoch,
                reminderInterval,
                binding.notesInput.text.toString(),
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

    private fun getReminderStartEpoch() : Long {
        return viewModel.getReminderStartEpoch(getReminderDateTime())
    }

    private fun isValidEntry(): Boolean {
        return isDetailValid() && isIntervalValid()
    }

    private fun isDetailValid(): Boolean {
        when {
            binding.nameInput.text.toString().isBlank() -> {
                Toast.makeText(context, "The name cannot be empty.", Toast.LENGTH_SHORT).show()
                binding.nameLabel.error = ""
                return false
            }
            binding.startDateInput.text.toString().isBlank() -> {
                Toast.makeText(context, "The start date cannot be empty.", Toast.LENGTH_SHORT)
                    .show()
                binding.startDateLabel.error = " "
                return false
            }
            getReminderDate().isBefore(LocalDate.now()) -> {
                Toast.makeText(context, "The start date cannot be in the past.", Toast.LENGTH_SHORT)
                    .show()
                binding.startDateLabel.error = " "
                return false
            }
            binding.startTimeInput.text.toString().isBlank() -> {
                Toast.makeText(context, "The start time cannot be empty.", Toast.LENGTH_SHORT)
                    .show()
                binding.startTimeLabel.error = " "
                return false
            }
            getReminderDateTime().isBefore(LocalDateTime.now()) -> {
                Toast.makeText(context, "The start time cannot be in the past.", Toast.LENGTH_SHORT)
                    .show()
                binding.startTimeLabel.error = " "
                return false
            }
            else -> return true
        }
    }

    private fun isIntervalValid(): Boolean {
        val MAX_YEARS = 10
        val MAX_DAYS = 364
        val MAX_HOURS = 23

        val years = binding.yearsInput.text.toString().toInt()
        val days = binding.daysInput.text.toString().toInt()
        val hours = binding.hoursInput.text.toString().toInt()

        when {
            years > MAX_YEARS -> {
                Toast.makeText(context, "The maximum years interval is 10.", Toast.LENGTH_SHORT).show()
                binding.yearsLabel.error = " "
                binding.yearsLabel.errorIconDrawable = null
                return false
            }
            days > MAX_DAYS -> {
                Toast.makeText(context, "The maximum days interval is 364.", Toast.LENGTH_SHORT).show()
                binding.daysLabel.error = " "
                binding.daysLabel.errorIconDrawable = null
                return false
            }
            hours > MAX_HOURS -> {
                Toast.makeText(context, "The maximum hours interval is 23.", Toast.LENGTH_SHORT).show()
                binding.hoursLabel.error = " "
                binding.hoursLabel.errorIconDrawable = null
                return false
            }
            years == 0 && days == 0 && hours == 0 -> {
                Toast.makeText(context, "The interval must have a time period.", Toast.LENGTH_SHORT).show()
                return false
            }
            else -> return true
        }
    }
}
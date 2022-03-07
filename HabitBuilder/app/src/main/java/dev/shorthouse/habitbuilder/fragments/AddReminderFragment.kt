package dev.shorthouse.habitbuilder.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dev.shorthouse.habitbuilder.BaseApplication
import dev.shorthouse.habitbuilder.R
import dev.shorthouse.habitbuilder.databinding.FragmentAddReminderBinding
import dev.shorthouse.habitbuilder.viewmodels.AddReminderViewModel
import dev.shorthouse.habitbuilder.viewmodels.AddReminderViewModelFactory
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddReminderBinding.inflate(inflater, container, false)
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

            repeatSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    intervalHeader.visibility = View.VISIBLE
                    yearsLabel.visibility = View.VISIBLE
                    daysLabel.visibility = View.VISIBLE
                    hoursLabel.visibility = View.VISIBLE
                } else {
                    intervalHeader.visibility = View.GONE
                    yearsLabel.visibility = View.GONE
                    daysLabel.visibility = View.GONE
                    hoursLabel.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_reminder_app_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_done -> {
                addReminder()
                return true
            }
            else -> super.onOptionsItemSelected(item)
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
                )
            )
        }

        timePicker.show(parentFragmentManager, getString(R.string.reminder_time_picker_tag))
    }

    private fun getTimePicker(): MaterialTimePicker {
        return MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText(getString(R.string.time_picker_title))
            .build()
    }

    private fun addReminder() {
        if (isValidEntry()) {
            val reminderStartEpoch = getReminderStartEpoch()

            val reminderInterval = if (!binding.repeatSwitch.isChecked) {
                null
            } else {
                viewModel.getReminderInterval(
                    binding.yearsInput.text.toString().toLongOrZero(),
                    binding.daysInput.text.toString().toLongOrZero(),
                    binding.hoursInput.text.toString().toLongOrZero(),
                )
            }

            viewModel.addReminder(
                binding.nameInput.text.toString(),
                reminderStartEpoch,
                reminderInterval,
                binding.notesInput.text.toString(),
                false
            )

            Toast.makeText(context, getString(R.string.toast_reminder_saved), Toast.LENGTH_SHORT)
                .show()
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

    private fun getReminderStartEpoch(): Long {
        return viewModel.getReminderStartEpoch(getReminderDateTime())
    }

    private fun isValidEntry(): Boolean {
        return isDetailValid() && isIntervalValid()
    }

    private fun isDetailValid(): Boolean {
        when {
            binding.nameInput.text.toString().isBlank() -> {
                Toast.makeText(context, "The name cannot be empty.", Toast.LENGTH_SHORT).show()
                binding.nameLabel.error = " "
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
        if (!binding.repeatSwitch.isChecked) {
            return true
        }

        val MAX_YEARS = 10
        val MAX_DAYS = 364
        val MAX_HOURS = 23

        val years = binding.yearsInput.text.toString().toLongOrZero()
        val days = binding.daysInput.text.toString().toLongOrZero()
        val hours = binding.hoursInput.text.toString().toLongOrZero()

        when {
            years > MAX_YEARS -> {
                Toast.makeText(context, "The maximum years interval is 10.", Toast.LENGTH_SHORT)
                    .show()
                setErrorOnTextInput(binding.yearsLabel)
                return false
            }
            days > MAX_DAYS -> {
                Toast.makeText(context, "The maximum days interval is 364.", Toast.LENGTH_SHORT)
                    .show()
                setErrorOnTextInput(binding.daysLabel)
                return false
            }
            hours > MAX_HOURS -> {
                Toast.makeText(context, "The maximum hours interval is 23.", Toast.LENGTH_SHORT)
                    .show()
                setErrorOnTextInput(binding.daysLabel)
                return false
            }
            years == 0L && days == 0L && hours == 0L -> {
                Toast.makeText(context, "The interval must have a time period.", Toast.LENGTH_SHORT)
                    .show()
                return false
            }
            else -> return true
        }
    }

    private fun String.toLongOrZero(): Long {
        return if (this.isBlank()) 0 else this.toLong()
    }

    private fun setErrorOnTextInput(textInput: TextInputLayout) {
        textInput.error = " "
        textInput.errorIconDrawable = null
    }
}
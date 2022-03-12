package dev.shorthouse.habitbuilder.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dev.shorthouse.habitbuilder.BaseApplication
import dev.shorthouse.habitbuilder.R
import dev.shorthouse.habitbuilder.databinding.FragmentAddReminderBinding
import dev.shorthouse.habitbuilder.viewmodels.AddReminderViewModel
import dev.shorthouse.habitbuilder.viewmodels.AddReminderViewModelFactory
import java.time.Instant

class AddReminderFragment : Fragment() {
    private lateinit var binding: FragmentAddReminderBinding

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
    ): View {
        binding = FragmentAddReminderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            addReminderFragment = this@AddReminderFragment
            viewmodel = viewModel
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_reminder_app_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_done -> {
                if (isValidEntry()) {
                    addReminder()
                    hideKeyboard()
                    navigateUp()
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addReminder() {
        val reminderName = binding.nameInput.text.toString()

        val reminderStartEpoch = viewModel.calculateReminderStartEpoch(
            getString(
                R.string.format_date_time,
                binding.startDateInput.text.toString(),
                binding.startTimeInput.text.toString()
            )
        )

        val reminderInterval = if (!binding.repeatSwitch.isChecked) {
            null
        } else {
            viewModel.convertReminderIntervalToSeconds(
                binding.yearsInput.text.toString().toLongOrZero(),
                binding.daysInput.text.toString().toLongOrZero(),
                binding.hoursInput.text.toString().toLongOrZero(),
            )
        }

        val reminderNotes = if (binding.notesInput.text.isNullOrBlank()) {
            null
        } else {
            binding.notesInput.text.toString()
        }

        val isArchived = false

        viewModel.addReminder(
            reminderName,
            reminderStartEpoch,
            reminderInterval,
            reminderNotes,
            isArchived
        )
    }

    fun displayDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.title_date_picker))
            .build()

        datePicker.addOnPositiveButtonClickListener { dateTimestamp ->
            binding.startDateInput.setText(
                viewModel.convertInstantToDateString(Instant.ofEpochMilli(dateTimestamp))
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

    private fun isValidEntry(): Boolean {
        return isDetailValid() && isIntervalValid()
    }

    private fun isDetailValid(): Boolean {
        val name = binding.nameInput.text.toString()
        val startDate = binding.startDateInput.text.toString()
        val reminderTime = binding.startTimeInput.text.toString()

        val isDetailValid = viewModel.isDetailValid(name, startDate, reminderTime)

        return if (isDetailValid) {
            isDetailValid
        } else {
            makeShortToast(viewModel.getDetailError(name, startDate))
            isDetailValid
        }
    }

    private fun isIntervalValid(): Boolean {
        val years = binding.yearsInput.text.toString().toLongOrZero()
        val days = binding.daysInput.text.toString().toLongOrZero()
        val hours = binding.hoursInput.text.toString().toLongOrZero()

        val isIntervalValid = viewModel.isIntervalValid(
            binding.repeatSwitch.isChecked,
            years,
            days,
            hours
        )

        return if (isIntervalValid) {
            isIntervalValid
        } else {
            makeShortToast(viewModel.getIntervalError(years, days, hours))
            isIntervalValid
        }
    }

    private fun makeShortToast(stringResId: Int) {
        Toast.makeText(
            context,
            getString(stringResId),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun String.toLongOrZero(): Long {
        return if (this.isBlank()) 0L else this.toLong()
    }
}
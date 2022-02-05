package dev.shorthouse.habitbuilder.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import dev.shorthouse.habitbuilder.BaseApplication
import dev.shorthouse.habitbuilder.databinding.AddHabitFragmentBinding
import dev.shorthouse.habitbuilder.ui.viewmodel.HabitViewModel
import dev.shorthouse.habitbuilder.ui.viewmodel.HabitViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class AddHabitFragment : Fragment() {
    private var _binding: AddHabitFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HabitViewModel by activityViewModels {
        HabitViewModelFactory(
            (activity?.application as BaseApplication).database.habitDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddHabitFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startDateInput.setOnClickListener {
            displayDatePicker()
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
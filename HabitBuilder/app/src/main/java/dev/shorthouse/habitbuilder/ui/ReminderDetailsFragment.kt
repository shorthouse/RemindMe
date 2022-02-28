package dev.shorthouse.habitbuilder.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import dev.shorthouse.habitbuilder.BaseApplication
import dev.shorthouse.habitbuilder.R
import dev.shorthouse.habitbuilder.databinding.FragmentAddReminderBinding
import dev.shorthouse.habitbuilder.databinding.FragmentReminderDetailsBinding
import dev.shorthouse.habitbuilder.ui.viewmodel.AddReminderViewModel
import dev.shorthouse.habitbuilder.ui.viewmodel.AddReminderViewModelFactory
import dev.shorthouse.habitbuilder.ui.viewmodel.ReminderDetailsViewModel
import dev.shorthouse.habitbuilder.ui.viewmodel.ReminderDetailsViewModelFactory

class ReminderDetailsFragment : Fragment() {
    private var _binding: FragmentReminderDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReminderDetailsViewModel by activityViewModels {
        ReminderDetailsViewModelFactory(
            (activity?.application as BaseApplication).database.reminderDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReminderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }
}
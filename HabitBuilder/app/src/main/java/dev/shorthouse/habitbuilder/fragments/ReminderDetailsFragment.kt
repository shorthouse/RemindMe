package dev.shorthouse.habitbuilder.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import dev.shorthouse.habitbuilder.BaseApplication
import dev.shorthouse.habitbuilder.databinding.FragmentReminderDetailsBinding
import dev.shorthouse.habitbuilder.model.Reminder
import dev.shorthouse.habitbuilder.viewmodels.ReminderDetailsViewModel
import dev.shorthouse.habitbuilder.viewmodels.ReminderDetailsViewModelFactory

class ReminderDetailsFragment : Fragment() {
    private var _binding: FragmentReminderDetailsBinding? = null
    private val binding get() = _binding!!

    private val navigationArgs: ReminderDetailsFragmentArgs by navArgs()

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
        binding.viewmodel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.id
        viewModel.getReminder(id).observe(this.viewLifecycleOwner) {
            binding.reminder = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
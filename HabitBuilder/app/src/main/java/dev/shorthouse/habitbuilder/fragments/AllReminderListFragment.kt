package dev.shorthouse.habitbuilder.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dev.shorthouse.habitbuilder.BaseApplication
import dev.shorthouse.habitbuilder.R
import dev.shorthouse.habitbuilder.adapter.AllReminderListAdapter
import dev.shorthouse.habitbuilder.databinding.FragmentActiveReminderListBinding
import dev.shorthouse.habitbuilder.databinding.FragmentAllReminderListBinding
import dev.shorthouse.habitbuilder.viewmodels.ActiveReminderListViewModel
import dev.shorthouse.habitbuilder.viewmodels.ActiveReminderListViewModelFactory
import dev.shorthouse.habitbuilder.viewmodels.AllReminderListViewModel
import dev.shorthouse.habitbuilder.viewmodels.AllReminderListViewModelFactory

class AllReminderListFragment : Fragment() {
    private var _binding: FragmentAllReminderListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AllReminderListViewModel by activityViewModels {
        AllReminderListViewModelFactory(
            (activity?.application as BaseApplication).database.reminderDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAllReminderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = AllReminderListAdapter { reminder ->
            val action = AllReminderListFragmentDirections
                .actionAllRemindersToReminderDetails(reminder.id)
            findNavController().navigate(action)
        }

        viewModel.reminders.observe(this.viewLifecycleOwner) {reminders ->
            adapter.submitList(reminders)
        }

        binding.apply {
            allReminderRecycler.adapter = adapter

            addReminderFab.setOnClickListener {
                val action = AllReminderListFragmentDirections
                    .actionAllRemindersToAddReminder()
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}



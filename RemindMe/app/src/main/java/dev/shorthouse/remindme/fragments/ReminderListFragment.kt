package dev.shorthouse.remindme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.adapter.ActiveReminderListAdapter
import dev.shorthouse.remindme.adapter.AllReminderListAdapter
import dev.shorthouse.remindme.databinding.FragmentReminderListBinding
import dev.shorthouse.remindme.utilities.RemindersFilter
import dev.shorthouse.remindme.viewmodel.ReminderListViewModel

@AndroidEntryPoint
class ReminderListFragment(private val filter: RemindersFilter) : Fragment() {
    private lateinit var binding: FragmentReminderListBinding

    private val viewModel: ReminderListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReminderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListAdapter()
    }

    private fun setListAdapter() {
        val recyclerAdapter = when (filter) {
            RemindersFilter.ACTIVE_REMINDERS -> ActiveReminderListAdapter(viewModel)
            else -> AllReminderListAdapter()
        }

        viewModel.remindersList = when (filter) {
            RemindersFilter.ACTIVE_REMINDERS -> viewModel.activeReminders
            else -> viewModel.allReminders
        }

        viewModel.remindersList.observe(viewLifecycleOwner) { reminders ->
            viewModel.remindersList.removeObservers(viewLifecycleOwner)
            recyclerAdapter.submitList(reminders)
            binding.reminderRecycler.adapter = recyclerAdapter
        }
    }
}

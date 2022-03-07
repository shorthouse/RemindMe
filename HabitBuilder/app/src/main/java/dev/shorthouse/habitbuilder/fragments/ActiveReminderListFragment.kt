package dev.shorthouse.habitbuilder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dev.shorthouse.habitbuilder.BaseApplication
import dev.shorthouse.habitbuilder.R
import dev.shorthouse.habitbuilder.adapter.ActiveReminderListAdapter
import dev.shorthouse.habitbuilder.databinding.FragmentActiveReminderListBinding
import dev.shorthouse.habitbuilder.viewmodels.ActiveReminderListViewModel
import dev.shorthouse.habitbuilder.viewmodels.ActiveReminderListViewModelFactory

class ActiveReminderListFragment : Fragment() {
    private var _binding: FragmentActiveReminderListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ActiveReminderListViewModel by activityViewModels {
        ActiveReminderListViewModelFactory(
            (activity?.application as BaseApplication).database.reminderDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentActiveReminderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ActiveReminderListAdapter(getAdapterClickListener())

        viewModel.activeReminders.observe(this.viewLifecycleOwner) { reminders ->
            adapter.submitList(reminders)
        }

        binding.apply {
            activeReminderRecycler.adapter = adapter

            addReminderFab.setOnClickListener {
                findNavController().navigate(
                    R.id.action_active_reminders_to_add_reminder
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getAdapterClickListener(): ActiveReminderListAdapter.ClickListener {
        return ActiveReminderListAdapter.ClickListener { reminder, itemId ->
            when (itemId) {
                R.id.active_reminder_container -> {
                    val action = ActiveReminderListFragmentDirections
                        .actionActiveRemindersToReminderDetails(reminder.id)
                    findNavController().navigate(action)
                }
                R.id.reminder_done -> {
                    updateDoneReminder(
                        reminder.id,
                        reminder.name,
                        reminder.startEpoch,
                        reminder.repeatInterval,
                        reminder.notes,
                    )
                }
            }
        }
    }

    private fun updateDoneReminder(
        id: Long,
        name: String,
        startEpoch: Long,
        repeatInterval: Long?,
        notes: String?,
    ) {
        viewModel.updateDoneReminder(
            id,
            name,
            startEpoch,
            repeatInterval,
            notes,
        )
    }
}
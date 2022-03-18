package dev.shorthouse.remindme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import dev.shorthouse.remindme.BaseApplication
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.adapter.ActiveReminderListAdapter
import dev.shorthouse.remindme.databinding.FragmentActiveReminderListBinding
import dev.shorthouse.remindme.viewmodel.ActiveReminderListViewModel
import dev.shorthouse.remindme.viewmodel.ActiveReminderListViewModelFactory

class ActiveReminderListFragment : Fragment() {
    private lateinit var binding: FragmentActiveReminderListBinding

    private val viewModel: ActiveReminderListViewModel by activityViewModels {
        ActiveReminderListViewModelFactory(
            (activity?.application as BaseApplication).database.reminderDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentActiveReminderListBinding.inflate(inflater, container, false)
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
                val action = ActiveReminderListFragmentDirections
                    .actionActiveRemindersToAddReminder()
                findNavController().navigate(action)
            }

            activeReminderRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    when {
                        dy > 0 && addReminderFab.isShown -> addReminderFab.hide()
                        dy < 0 && !addReminderFab.isShown -> addReminderFab.show()
                    }
                }
            })
        }
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
                        reminder.isNotificationSent,
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
        isNotificationSent: Boolean,
    ) {
        viewModel.updateDoneReminder(
            id,
            name,
            startEpoch,
            repeatInterval,
            notes,
            isNotificationSent,
        )
    }
}
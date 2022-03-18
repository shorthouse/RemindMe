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
import dev.shorthouse.remindme.adapter.AllReminderListAdapter
import dev.shorthouse.remindme.databinding.FragmentAllReminderListBinding
import dev.shorthouse.remindme.viewmodel.AllReminderListViewModel
import dev.shorthouse.remindme.viewmodel.AllReminderListViewModelFactory


class AllReminderListFragment : Fragment() {
    private lateinit var binding: FragmentAllReminderListBinding

    private val viewModel: AllReminderListViewModel by activityViewModels {
        AllReminderListViewModelFactory(
            (activity?.application as BaseApplication).database.reminderDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllReminderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = AllReminderListAdapter { reminder ->
            val action = AllReminderListFragmentDirections
                .actionAllRemindersToReminderDetails(reminder.id)
            findNavController().navigate(action)
        }

        viewModel.reminders.observe(this.viewLifecycleOwner) { reminders ->
            adapter.submitList(reminders)
        }

        binding.apply {
            allReminderRecycler.adapter = adapter

            addReminderFab.setOnClickListener {
                val action = AllReminderListFragmentDirections
                    .actionAllRemindersToAddReminder()
                findNavController().navigate(action)
            }

            allReminderRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
}



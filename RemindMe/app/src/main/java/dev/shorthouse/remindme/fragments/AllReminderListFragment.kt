package dev.shorthouse.remindme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.adapter.AllReminderListAdapter
import dev.shorthouse.remindme.databinding.FragmentAllReminderListBinding
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.viewmodel.AllReminderListViewModel
import dev.shorthouse.remindme.viewmodel.ReminderListViewPagerViewModel

@AndroidEntryPoint
class AllReminderListFragment : Fragment() {
    private lateinit var binding: FragmentAllReminderListBinding

    private val viewModel: AllReminderListViewModel by viewModels()
    private val viewPagerViewModel: ReminderListViewPagerViewModel by activityViewModels()

    private lateinit var listAdapter: AllReminderListAdapter

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
        setListAdapter()
        setListData()
    }

    private fun setListAdapter() {
        listAdapter = AllReminderListAdapter()
        binding.allReminderRecycler.adapter = listAdapter
    }

    private fun setListData() {
        viewModel.getReminders(viewPagerViewModel.currentSort, viewPagerViewModel.currentFilter)
            .observe(viewLifecycleOwner) { reminders ->
                reminders?.let {
                    listAdapter.submitList(reminders)
                    displayListState(reminders)
                }

            }
    }

    private fun displayListState(reminders: List<Reminder>) {
        if (reminders.isNotEmpty()) {
            displayReminderList()
        } else {
            if (viewPagerViewModel.currentFilter.value?.isNotEmpty() == true) {
                displaySearchEmptyState()
            } else {
                displayEmptyState()
            }
        }
    }

    private fun displayEmptyState() {
        binding.emptyStateGroup.visibility = View.VISIBLE
        binding.emptyStateSearchGroup.visibility = View.GONE
        binding.allReminderRecycler.visibility = View.GONE

    }

    private fun displaySearchEmptyState() {
        binding.emptyStateSearchGroup.visibility = View.VISIBLE
        binding.emptyStateGroup.visibility = View.GONE
        binding.allReminderRecycler.visibility = View.GONE
    }

    private fun displayReminderList() {
        binding.allReminderRecycler.visibility = View.VISIBLE
        binding.emptyStateGroup.visibility = View.GONE
        binding.emptyStateSearchGroup.visibility = View.GONE
    }
}
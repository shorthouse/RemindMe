package dev.shorthouse.remindme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.adapter.AllReminderListAdapter
import dev.shorthouse.remindme.databinding.FragmentAllListBinding
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.viewmodel.AllListViewModel
import dev.shorthouse.remindme.viewmodel.ListContainerViewModel

@AndroidEntryPoint
class AllListFragment : Fragment() {
    private lateinit var binding: FragmentAllListBinding

    private val viewModel: AllListViewModel by viewModels()
    private val viewPagerViewModel: ListContainerViewModel by activityViewModels()

    private lateinit var listAdapter: AllReminderListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllListBinding.inflate(inflater, container, false)
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
                    displayListState(reminders)
                    listAdapter.submitList(reminders)
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

    private fun displayReminderList() {
        binding.allReminderRecycler.visibility = View.VISIBLE
        binding.emptyStateGroup.visibility = View.GONE
        binding.emptyStateSearchGroup.visibility = View.GONE
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


}
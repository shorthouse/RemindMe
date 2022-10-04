package dev.shorthouse.remindme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.adapter.ActiveReminderListAdapter
import dev.shorthouse.remindme.databinding.FragmentActiveListBinding
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.viewmodel.ActiveListViewModel
import dev.shorthouse.remindme.viewmodel.ListViewPagerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration

@AndroidEntryPoint
class ActiveListFragment : Fragment() {
    private lateinit var binding: FragmentActiveListBinding

    private val viewModel: ActiveListViewModel by viewModels()
    private val viewPagerViewModel: ListViewPagerViewModel by activityViewModels()

    private lateinit var listAdapter: ActiveReminderListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentActiveListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListAdapter()
        setListData()
        startReminderRefreshCoroutine()
    }

    private fun setListAdapter() {
        listAdapter = ActiveReminderListAdapter(viewModel)
        binding.activeReminderRecycler.adapter = listAdapter
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

    private fun startReminderRefreshCoroutine() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            delay(viewModel.getMillisUntilNextMinute())

            while (true) {
                launch {
                    viewModel.updateCurrentTime()
                }
                delay(Duration.ofMinutes(1).toMillis())
            }
        }.start()
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
        binding.activeReminderRecycler.visibility = View.VISIBLE
        binding.emptyStateGroup.visibility = View.GONE
        binding.emptyStateSearchGroup.visibility = View.GONE
    }

    private fun displayEmptyState() {
        binding.emptyStateGroup.visibility = View.VISIBLE
        binding.emptyStateSearchGroup.visibility = View.GONE
        binding.activeReminderRecycler.visibility = View.GONE
    }

    private fun displaySearchEmptyState() {
        binding.emptyStateSearchGroup.visibility = View.VISIBLE
        binding.emptyStateGroup.visibility = View.GONE
        binding.activeReminderRecycler.visibility = View.GONE
    }
}
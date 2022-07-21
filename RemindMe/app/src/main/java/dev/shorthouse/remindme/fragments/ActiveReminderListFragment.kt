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
import dev.shorthouse.remindme.databinding.FragmentActiveReminderListBinding
import dev.shorthouse.remindme.viewmodel.ActiveReminderListViewModel
import dev.shorthouse.remindme.viewmodel.ReminderListViewPagerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration


@AndroidEntryPoint
class ActiveReminderListFragment : Fragment() {
    private lateinit var binding: FragmentActiveReminderListBinding

    private val viewModel: ActiveReminderListViewModel by viewModels()
    private val viewPagerViewModel: ReminderListViewPagerViewModel by activityViewModels()

    private lateinit var listAdapter: ActiveReminderListAdapter

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
        setListAdapter()
        observeListData()
        startReminderRefreshCoroutine()
    }

    private fun setListAdapter() {
        listAdapter = ActiveReminderListAdapter(viewModel)
        binding.activeReminderRecycler.adapter = listAdapter
    }

    private fun observeListData() {
        viewModel.remindersListData(viewPagerViewModel.currentSort, viewPagerViewModel.currentFilter)
            .observe(viewLifecycleOwner) { reminders ->
                listAdapter.submitList(reminders)
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
}
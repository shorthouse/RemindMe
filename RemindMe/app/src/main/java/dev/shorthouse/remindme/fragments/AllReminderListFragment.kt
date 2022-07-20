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
        observeListData()
    }

    private fun setListAdapter() {
        listAdapter = AllReminderListAdapter()
        binding.allReminderRecycler.adapter = listAdapter
    }

    private fun observeListData() {
        viewModel.getReminders(viewPagerViewModel.currentSort, viewPagerViewModel.currentFilter)
            .observe(viewLifecycleOwner) { reminders ->
                listAdapter.submitList(reminders)
            }
    }
}
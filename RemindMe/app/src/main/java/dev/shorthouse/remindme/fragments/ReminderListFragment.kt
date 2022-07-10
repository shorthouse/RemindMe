package dev.shorthouse.remindme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.adapter.ActiveReminderListAdapter
import dev.shorthouse.remindme.adapter.AllReminderListAdapter
import dev.shorthouse.remindme.databinding.FragmentReminderListBinding
import dev.shorthouse.remindme.utilities.RemindersFilter
import dev.shorthouse.remindme.utilities.RemindersSort
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

        setReminderFilter()
        setupReminderListObserver()
    }

    private fun setReminderFilter() {
        viewModel.currentFilter = filter
    }

    private fun setupReminderListObserver() {
        viewModel.remindersList.observe(viewLifecycleOwner) { reminders ->
            val recyclerAdapter = when (viewModel.currentFilter) {
                RemindersFilter.ACTIVE_REMINDERS -> ActiveReminderListAdapter(viewModel)
                else -> AllReminderListAdapter()
            }

            recyclerAdapter.submitList(reminders)
            binding.reminderRecycler.adapter = recyclerAdapter
        }
    }


}

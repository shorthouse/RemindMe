package dev.shorthouse.remindme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.adapter.AllListAdapter
import dev.shorthouse.remindme.compose.screen.ReminderListAllScreen
import dev.shorthouse.remindme.databinding.FragmentAllListBinding
import dev.shorthouse.remindme.viewmodel.AllListViewModel
import dev.shorthouse.remindme.viewmodel.ListContainerViewModel
import dev.shorthouse.remindme.viewmodel.ReminderListViewModel

@AndroidEntryPoint
class AllListFragment : Fragment() {
    private lateinit var binding: FragmentAllListBinding

    private val viewModel: AllListViewModel by viewModels()
    private val listContainerViewModel: ListContainerViewModel by activityViewModels()

    private lateinit var listAdapter: AllListAdapter

    private val reminderListViewModel: ReminderListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransitionAnimations()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllListBinding.inflate(inflater, container, false).apply {
            allReminderListComposeView.apply {
                setViewCompositionStrategy(
                    ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
                )

                setContent {
                    MdcTheme {
                        ReminderListAllScreen(
                            reminderListViewModel = reminderListViewModel
                        )
                    }
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setListAdapter()
//        setListData()
    }

    private fun setTransitionAnimations() {
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

//    private fun setListAdapter() {
//        listAdapter = AllListAdapter()
//        binding.allReminderRecycler.adapter = listAdapter
//    }

//    private fun setListData() {
//        viewModel.getReminders(
//            listContainerViewModel.currentSort,
//            listContainerViewModel.currentFilter
//        )
//            .observe(viewLifecycleOwner) { reminders ->
//                reminders?.let {
//                    submitAdapterList(reminders)
//                    displayListState(reminders)
//                }
//            }
//    }

//    private fun submitAdapterList(reminders: List<Reminder>) {
//        val layoutManager = binding.allReminderRecycler.layoutManager
//
//        val savedListScrollPosition = layoutManager?.onSaveInstanceState()
//        listAdapter.submitList(reminders) {
//            layoutManager?.onRestoreInstanceState(savedListScrollPosition)
//        }
//    }

//    private fun displayListState(reminders: List<Reminder>) {
//        hideOldListState()
//
//        val newListState = when {
//            reminders.isNotEmpty() -> binding.allReminderRecycler
//            listContainerViewModel.currentFilter.value?.isNotEmpty() == true -> binding.emptyStateSearch
//            else -> binding.emptyState
//        }
//
//        newListState.visibility = View.VISIBLE
//    }

//    private fun hideOldListState() {
//        binding.allReminderRecycler.visibility = View.GONE
//        binding.emptyState.visibility = View.GONE
//        binding.emptyStateSearch.visibility = View.GONE
//    }
}

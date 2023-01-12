package dev.shorthouse.remindme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.compose.screen.ReminderListActiveScreen
import dev.shorthouse.remindme.databinding.FragmentActiveListBinding
import dev.shorthouse.remindme.viewmodel.ListActiveViewModel

@AndroidEntryPoint
class ListActiveFragment : Fragment() {
    private lateinit var binding: FragmentActiveListBinding
    private val listActiveViewModel: ListActiveViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransitionAnimations()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentActiveListBinding.inflate(inflater, container, false).apply {
            listActiveComposeView.apply {
                setViewCompositionStrategy(
                    ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
                )

                val onNavigate: (Long) -> Unit = { reminderId ->
                    val action = ListHomeFragmentDirections.actionListContainerToDetails(reminderId)
                    findNavController().navigate(action)
                }

                setContent {
                    MdcTheme {
                        ReminderListActiveScreen(
                            listActiveViewModel = listActiveViewModel,
                            onNavigate = onNavigate
                        )
                    }
                }
            }
        }

        return binding.root
    }

    private fun setTransitionAnimations() {
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        setListAdapter()
//        setListData()
//        startReminderRefreshCoroutine()
//    }
//
//
//    private fun setListAdapter() {
//        listAdapter = ActiveListAdapter(viewModel)
//        binding.activeReminderRecycler.adapter = listAdapter
//    }
//
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
//
//    private fun submitAdapterList(reminders: List<Reminder>) {
//        val layoutManager = binding.activeReminderRecycler.layoutManager
//        val savedListScrollPosition = layoutManager?.onSaveInstanceState()
//
//        listAdapter.submitList(reminders) {
//            layoutManager?.onRestoreInstanceState(savedListScrollPosition)
//        }
//    }
//
//    private fun displayListState(reminders: List<Reminder>) {
//        hideOldListState()
//
//        val newListState = when {
//            reminders.isNotEmpty() -> binding.activeReminderRecycler
//            listContainerViewModel.currentFilter.value?.isNotEmpty() == true -> binding.emptyStateSearch
//            else -> binding.emptyState
//        }
//
//        newListState.visibility = View.VISIBLE
//    }
//
//    private fun hideOldListState() {
//        binding.activeReminderRecycler.visibility = View.GONE
//        binding.emptyState.visibility = View.GONE
//        binding.emptyStateSearch.visibility = View.GONE
//    }
//
//    private fun startReminderRefreshCoroutine() {
//        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
//            delay(viewModel.getMillisUntilNextMinute())
//
//            while (true) {
//                launch {
//                    viewModel.updateCurrentTime()
//                }
//                delay(Duration.ofMinutes(1).toMillis())
//            }
//        }.start()
//    }
}

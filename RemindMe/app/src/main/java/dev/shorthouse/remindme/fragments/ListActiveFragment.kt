package dev.shorthouse.remindme.fragments

//import android.os.Bundle
//import android.view.View
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.lifecycleScope
//import dagger.hilt.android.AndroidEntryPoint
//import dev.shorthouse.remindme.model.Reminder
//
//@AndroidEntryPoint
//class ListActiveFragment : Fragment() {
//        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        setListAdapter()
//        setListData()
//        startReminderRefreshCoroutine()
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
//}

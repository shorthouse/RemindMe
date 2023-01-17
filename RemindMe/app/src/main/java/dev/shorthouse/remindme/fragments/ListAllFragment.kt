package dev.shorthouse.remindme.fragments

//import android.view.View
//import androidx.fragment.app.Fragment
//import dagger.hilt.android.AndroidEntryPoint
//import dev.shorthouse.remindme.model.Reminder
//
//@AndroidEntryPoint
//class ListAllFragment : Fragment() {
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
//        val layoutManager = binding.allReminderRecycler.layoutManager
//
//        val savedListScrollPosition = layoutManager?.onSaveInstanceState()
//        listAdapter.submitList(reminders) {
//            layoutManager?.onRestoreInstanceState(savedListScrollPosition)
//        }
//    }
//
//    private fun displayListState(reminders: List<Reminder>) {
//        val newListState = when {
//            reminders.isNotEmpty() -> binding.allReminderRecycler
//            listContainerViewModel.currentFilter.value?.isNotEmpty() == true -> binding.emptyStateSearch
//            else -> binding.emptyState
//        }
//
//        newListState.visibility = View.VISIBLE
//    }
//}

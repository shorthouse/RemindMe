package dev.shorthouse.remindme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.adapter.ActiveReminderListAdapter
import dev.shorthouse.remindme.adapter.AllReminderListAdapter
import dev.shorthouse.remindme.databinding.FragmentReminderListBinding
import dev.shorthouse.remindme.utilities.RemindersFilter
import dev.shorthouse.remindme.utilities.RemindersSort
import dev.shorthouse.remindme.viewmodel.ReminderListViewModel


@AndroidEntryPoint
class ReminderListFragment : Fragment() {
    private lateinit var binding: FragmentReminderListBinding

    private val viewModel: ReminderListViewModel by viewModels()

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

        setupListeners()
        setupReminderListObserver()
        setupBottomDrawerFilter()
        setupBottomDrawerSort()
    }

    private fun setupListeners() {
        binding.apply {
            addReminderFab.setOnClickListener { navigateToAddEditReminder() }

            reminderRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) addReminderFab.hide() else if (dy < 0) addReminderFab.show()
                }
            })
        }
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

    private fun setupBottomDrawerSort() {
        binding.apply {
            val bottomSheetBehavior = BottomSheetBehavior.from(navigationViewListSort)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            val selectedMenuItem = when (viewModel.currentSort) {
                RemindersSort.NEWEST_FIRST -> R.id.drawer_sort_newest_first
                else -> R.id.drawer_sort_oldest_first
            }
            navigationViewListSort.setCheckedItem(selectedMenuItem)

            bottomAppBar.setOnMenuItemClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                true
            }

            navigationViewListSort.setNavigationItemSelectedListener { menuItem ->
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

                if (menuItem.itemId == navigationViewListSort.checkedItem?.itemId) {
                    return@setNavigationItemSelectedListener true
                }

                val sortOrder = when (menuItem.itemId) {
                    R.id.drawer_sort_newest_first -> RemindersSort.NEWEST_FIRST
                    else -> RemindersSort.OLDEST_FIRST
                }

                viewModel.sortReminderList(sortOrder)
                navigationViewListSort.setCheckedItem(menuItem.itemId)

                true
            }


            val backButtonCallback =
                requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                }

            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    scrim.setBackgroundColor(viewModel.getScrimBackgroundColour(slideOffset))
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        scrim.setOnClickListener {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        }
                        scrim.bringToFront()
                        backButtonCallback.isEnabled = true
                    } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        scrim.setOnClickListener(null)
                        reminderRecycler.bringToFront()
                        backButtonCallback.isEnabled = false
                    }
                }
            })
        }
    }

    private fun setupBottomDrawerFilter() {
        binding.apply {
            val bottomSheetBehavior = BottomSheetBehavior.from(navigationViewListFilter)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            val selectedMenuItem = when (viewModel.currentFilter) {
                RemindersFilter.ACTIVE_REMINDERS -> R.id.drawer_active_reminders
                else -> R.id.drawer_all_reminders
            }
            navigationViewListFilter.setCheckedItem(selectedMenuItem)

            bottomAppBar.setNavigationOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            navigationViewListFilter.setNavigationItemSelectedListener { menuItem ->
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

                if (menuItem.itemId == navigationViewListFilter.checkedItem?.itemId) {
                    return@setNavigationItemSelectedListener true
                }

                val filterType = when (menuItem.itemId) {
                    R.id.drawer_active_reminders -> RemindersFilter.ACTIVE_REMINDERS
                    else -> RemindersFilter.ALL_REMINDERS
                }

                viewModel.filterReminderList(filterType)
                navigationViewListSort.setCheckedItem(menuItem.itemId)

                true
            }

            val backButtonCallback =
                requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                }

            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    scrim.setBackgroundColor(viewModel.getScrimBackgroundColour(slideOffset))
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        scrim.setOnClickListener {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        }
                        scrim.bringToFront()
                        backButtonCallback.isEnabled = true
                    } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        scrim.setOnClickListener(null)
                        reminderRecycler.bringToFront()
                        backButtonCallback.isEnabled = false
                    }
                }
            })

        }
    }

    private fun navigateToAddEditReminder() {
        val action = ReminderListFragmentDirections
            .actionReminderListToAddEditReminder()
        findNavController().navigate(action)
    }

}

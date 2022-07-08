package dev.shorthouse.remindme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import dev.shorthouse.remindme.utilities.FILTER_ACTIVE_REMINDER_LIST
import dev.shorthouse.remindme.utilities.FILTER_ALL_REMINDER_LIST
import dev.shorthouse.remindme.utilities.SORT_NEWEST_FIRST
import dev.shorthouse.remindme.utilities.SORT_OLDEST_FIRST
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
        setupObservers()
        //setupListAdapter()
        setupBottomNavigationDrawer()
        setupBottomDrawerSort()
    }

    private fun setupListeners() {
        binding.apply {
            addReminderFab.setOnClickListener { navigateToAddEditReminder() }

            bottomAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.bottom_app_bar_sort -> {
                        Toast.makeText(context, "Sort icon clicked!", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }

            reminderRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) addReminderFab.hide() else if (dy < 0) addReminderFab.show()
                }
            })
        }
    }

    private fun setupObservers() {
        viewModel.remindersList.observe(viewLifecycleOwner) { reminders ->
            val recyclerAdapter = when (viewModel.currentFilter) {
                FILTER_ACTIVE_REMINDER_LIST -> ActiveReminderListAdapter(viewModel)
                else -> AllReminderListAdapter()
            }

            recyclerAdapter.submitList(reminders)
        }
    }

//    private fun setupListAdapter() {
//        when (viewModel.reminderAdapterState) {
//            FILTER_ALL_REMINDER_LIST -> setAdapterAllReminder()
//            else -> setAdapterActiveReminder()
//        }
//    }

//    private fun setAdapterActiveReminder() {
//        val adapter = ActiveReminderListAdapter(viewModel)
//        viewModel.activeReminders.observe(viewLifecycleOwner) { reminders ->
//            adapter.submitList(reminders)
//        }
//
//        binding.reminderRecycler.adapter = adapter
//        binding.navigationViewListFilter.setCheckedItem(R.id.drawer_active_reminders)
//    }
//
//    private fun setAdapterAllReminder() {
//        val adapter = AllReminderListAdapter()
//        viewModel.allReminders.observe(viewLifecycleOwner) { reminders ->
//            adapter.submitList(reminders)
//        }
//
//        binding.reminderRecycler.adapter = adapter
//        binding.navigationViewListFilter.setCheckedItem(R.id.drawer_all_reminders)
//    }

    private fun setupBottomDrawerSort() {
        binding.apply {
            val bottomSheetBehavior = BottomSheetBehavior.from(navigationViewListSort)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            bottomAppBar.setOnMenuItemClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                true
            }

            navigationViewListSort.setNavigationItemSelectedListener { menuItem ->
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

                if (menuItem.itemId != navigationViewListSort.checkedItem?.itemId) {
                    if (menuItem.itemId == R.id.drawer_sort_newest_first) {
                        viewModel.setReminderList(viewModel.currentFilter, SORT_NEWEST_FIRST)
                        navigationViewListSort.setCheckedItem(R.id.drawer_sort_newest_first)
                    } else if (menuItem.itemId == R.id.drawer_sort_oldest_first) {
                        viewModel.setReminderList(viewModel.currentFilter, SORT_OLDEST_FIRST) //TODO combine this just set var to sort and have one call to set reminder list
                        navigationViewListSort.setCheckedItem(R.id.drawer_sort_oldest_first)
                    }
                }

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

    private fun setupBottomNavigationDrawer() {
        binding.apply {
            val bottomSheetBehavior = BottomSheetBehavior.from(navigationViewListFilter)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            bottomAppBar.setNavigationOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            navigationViewListFilter.setNavigationItemSelectedListener { menuItem ->
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

                if (menuItem.itemId != navigationViewListFilter.checkedItem?.itemId) {
                    if (menuItem.itemId == R.id.drawer_active_reminders) {
                        viewModel.setReminderList(FILTER_ACTIVE_REMINDER_LIST, viewModel.currentSort)
                        navigationViewListFilter.setCheckedItem(R.id.drawer_active_reminders)
                    } else if (menuItem.itemId == R.id.drawer_all_reminders) {
                        viewModel.setReminderList(FILTER_ALL_REMINDER_LIST, viewModel.currentSort)
                        navigationViewListFilter.setCheckedItem(R.id.drawer_all_reminders)
                    }
                }

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

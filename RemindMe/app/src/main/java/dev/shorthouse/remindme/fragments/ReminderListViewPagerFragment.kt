package dev.shorthouse.remindme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.adapter.ReminderListPagerAdapter
import dev.shorthouse.remindme.databinding.FragmentReminderListBinding
import dev.shorthouse.remindme.databinding.FragmentViewPagerBinding
import dev.shorthouse.remindme.utilities.RemindersFilter
import dev.shorthouse.remindme.utilities.RemindersSort
import dev.shorthouse.remindme.viewmodel.ReminderListViewModel

@AndroidEntryPoint
class ReminderListViewPagerFragment : Fragment() {
    private lateinit var binding: FragmentViewPagerBinding

    private val viewModel: ReminderListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewPagerBinding.inflate(inflater, container, false)
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        viewPager.adapter = ReminderListPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()

        setupListeners()
        setupBottomDrawerSort()

        return binding.root
    }

    private fun setupListeners() {
        binding.apply {
            addReminderFab.setOnClickListener { navigateToAddEditReminder() }
        }
    }

    private fun navigateToAddEditReminder() {
        val action = ReminderListViewPagerFragmentDirections
            .actionReminderListViewPagerToAddEditReminder()
        findNavController().navigate(action)
    }

    private fun getTabTitle(position: Int): String {
        return when (RemindersFilter.values()[position]) {
            RemindersFilter.ACTIVE_REMINDERS -> getString(R.string.active_reminders)
            RemindersFilter.ALL_REMINDERS -> getString(R.string.all_reminders)
        }
    }

    private fun setupBottomDrawerSort() {
        //TODO break this down into functions
        binding.apply {
            val bottomSheetSort = BottomSheetBehavior.from(navigationViewListSort)
            bottomSheetSort.hide()

            //TODO Map current sort to an id
            val selectedMenuItem = when (viewModel.currentSort) {
                RemindersSort.NEWEST_FIRST -> R.id.drawer_sort_newest_first
                else -> R.id.drawer_sort_oldest_first
            }
            navigationViewListSort.setCheckedItem(selectedMenuItem)

            bottomAppBar.setOnMenuItemClickListener {
                bottomSheetSort.show()
                true
            }

            navigationViewListSort.setNavigationItemSelectedListener { menuItem ->
                bottomSheetSort.hide()

                if (menuItem.itemId == navigationViewListSort.checkedItem?.itemId) {
                    return@setNavigationItemSelectedListener true
                }

                //TODO map item id to sort enum
                val sortOrder = when (menuItem.itemId) {
                    R.id.drawer_sort_newest_first -> RemindersSort.NEWEST_FIRST
                    else -> RemindersSort.OLDEST_FIRST
                }

                viewModel.sortReminderList(sortOrder)
                navigationViewListSort.setCheckedItem(menuItem.itemId)

                true
            }

            //TODO add extension functions for isShown or isHidden
            val backButtonCallback =
                requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                    if (bottomSheetSort.state == BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetSort.hide()
                    }
                }

            bottomSheetSort.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    scrim.setBackgroundColor(viewModel.getScrimBackgroundColour(slideOffset))
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        scrim.setOnClickListener { bottomSheetSort.hide() }
                        backButtonCallback.isEnabled = true
                    } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        scrim.isClickable = false
                        backButtonCallback.isEnabled = false
                    }
                }
            })
        }
    }

    fun BottomSheetBehavior<NavigationView>.hide() {
        this.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun BottomSheetBehavior<NavigationView>.show() {
        this.state = BottomSheetBehavior.STATE_EXPANDED
    }
}
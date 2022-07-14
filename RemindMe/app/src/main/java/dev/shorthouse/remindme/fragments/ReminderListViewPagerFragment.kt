package dev.shorthouse.remindme.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.adapter.ACTIVE_REMINDERS_PAGE_INDEX
import dev.shorthouse.remindme.adapter.ALL_REMINDERS_PAGE_INDEX
import dev.shorthouse.remindme.adapter.ReminderListPagerAdapter
import dev.shorthouse.remindme.databinding.FragmentViewPagerBinding
import dev.shorthouse.remindme.utilities.hide
import dev.shorthouse.remindme.utilities.isShown
import dev.shorthouse.remindme.utilities.show
import dev.shorthouse.remindme.viewmodel.ReminderListViewPagerViewModel

@AndroidEntryPoint
class ReminderListViewPagerFragment : Fragment() {
    private lateinit var binding: FragmentViewPagerBinding

    private val viewModel: ReminderListViewPagerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabLayout()
        setupToolbar()
        setupListeners()
        setupObservers()
        setupBottomDrawerSort()
    }

    private fun setupToolbar() {
        binding.toolbar.setupWithNavController(findNavController())

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_search -> {
                    Log.d("HDS", "search clicked")
                    displayToast(R.string.toast_search_icon_clicked)
                    true
                }
                R.id.action_sort -> {
                    getBottomSheetSort().show()
                    true
                }
                else -> false
            }
        }

        val searchMenuItem = binding.toolbar.menu.findItem(R.id.action_search)
        val searchActionView = searchMenuItem.actionView as SearchView

        searchActionView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.d("HDS", "text submit, query is: $query")

                // Close draw (close to an icon instead of being expanded)
                if (!searchActionView.isIconified) {
                    searchActionView.isIconified = true
                }

                searchMenuItem.collapseActionView() // Collapse action view? Is this needed
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                Log.d("HDS", "new text, string is: $s")
                return false
            }
        })
    }

    private fun setupTabLayout() {
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        viewPager.adapter = ReminderListPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()
    }

    private fun setupListeners() {
        binding.apply {
            addReminderFab.setOnClickListener {
                navigateToAddEditReminder()
            }

            navigationViewListSort.setNavigationItemSelectedListener { menuItem ->
                getBottomSheetSort().hide()

                if (isDifferentMenuItemClicked(menuItem)) {
                    viewModel.menuItemToSortMap[menuItem.itemId]?.let { sort ->
                        viewModel.currentSort.value = sort
                    }
                }

                true
            }
        }
    }

    private fun setupObservers() {
        viewModel.currentSort.observe(viewLifecycleOwner) { sort ->
            viewModel.sortToMenuItemMap[sort]?.let { menuItemId ->
                binding.navigationViewListSort.setCheckedItem(menuItemId)
            }
        }
    }

    private fun setupBottomDrawerSort() {
        binding.apply {
            val bottomSheetSort = getBottomSheetSort()
            bottomSheetSort.hide()

            val backButtonCallback =
                requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                    if (bottomSheetSort.isShown()) {
                        bottomSheetSort.hide()
                    }
                }

            bottomSheetSort.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        enableScrim(backButtonCallback)
                    } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        disableScrim(backButtonCallback)
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    setScrimColor(slideOffset)
                }
            })
        }
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            ACTIVE_REMINDERS_PAGE_INDEX -> getString(R.string.active_reminders)
            ALL_REMINDERS_PAGE_INDEX -> getString(R.string.all_reminders)
            else -> null
        }
    }

    private fun navigateToAddEditReminder() {
        val action = ReminderListViewPagerFragmentDirections
            .actionReminderListViewPagerToAddEditReminder()
        findNavController().navigate(action)
    }

    private fun enableScrim(backButtonCallback: OnBackPressedCallback) {
        binding.scrim.setOnClickListener { getBottomSheetSort().hide() }
        backButtonCallback.isEnabled = true
    }

    private fun disableScrim(backButtonCallback: OnBackPressedCallback) {
        binding.scrim.isClickable = false
        backButtonCallback.isEnabled = false
    }

    private fun setScrimColor(slideOffset: Float) {
        binding.scrim.setBackgroundColor(viewModel.getScrimBackgroundColour(slideOffset))
    }

    private fun getBottomSheetSort(): BottomSheetBehavior<NavigationView> {
        return BottomSheetBehavior.from(binding.navigationViewListSort)
    }

    private fun isDifferentMenuItemClicked(menuItem: MenuItem): Boolean {
        return menuItem.itemId != binding.navigationViewListSort.checkedItem?.itemId
    }

    private fun displayToast(stringResId: Int) {
        Toast.makeText(
            context,
            getString(stringResId),
            Toast.LENGTH_SHORT
        ).show()
    }
}
package dev.shorthouse.remindme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.databinding.FragmentListContainerBinding
import dev.shorthouse.remindme.utilities.hide
import dev.shorthouse.remindme.utilities.hideKeyboard
import dev.shorthouse.remindme.utilities.show
import dev.shorthouse.remindme.viewmodel.ListContainerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListContainerFragment : Fragment() {
    private lateinit var binding: FragmentListContainerBinding
    private val viewModel: ListContainerViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.transition_duration_medium).toLong()
            excludeTarget(R.id.app_bar, true)
            excludeTarget(R.id.bottom_app_bar, true)
            excludeTarget(R.id.add_reminder_fab, true)
        }

        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.transition_duration_medium).toLong()
            excludeTarget(R.id.app_bar, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListFragment()
        setupToolbar()
        setupBottomAppBar()
        setupBottomSheetNavigation()
        setupBottomSheetSort()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        binding.toolbar.menu.findItem(R.id.action_search).collapseActionView()
    }

    private fun setupToolbar() {
        setupSearchView()
    }

    private fun setupSearchView() {
        val searchViewItem = binding.toolbar.menu.findItem(R.id.action_search)
        val searchView = searchViewItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(filter: String): Boolean {
                viewModel.currentFilter.value = filter
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                hideKeyboard()
                return true
            }
        })

        val overrideBackCallback =
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, false) {
                searchViewItem.collapseActionView()
            }

        val expandListener = object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                binding.bottomAppBar.performHide()
                binding.addReminderFab.hide()
                overrideBackCallback.isEnabled = true
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                binding.bottomAppBar.performShow()
                binding.addReminderFab.show()
                overrideBackCallback.isEnabled = false
                return true
            }
        }

        searchViewItem?.setOnActionExpandListener(expandListener)
    }

    private fun setupBottomAppBar() {
        binding.apply {
            bottomAppBar.setNavigationOnClickListener {
                getBottomSheetNavigation().show()
            }

            bottomAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_sort -> {
                        getBottomSheetSort().show()
                        true
                    }
                    else -> false
                }
            }

            addReminderFab.setOnClickListener {
                navigateToAddEditReminder()
            }
        }
    }

    private fun setupBottomSheetNavigation() {
        val bottomSheetNavigationBehaviour = getBottomSheetNavigation()
        bottomSheetNavigationBehaviour.hide()
        binding.bottomSheetNavigation.setCheckedItem(viewModel.bottomSheetListSelection)

        binding.bottomSheetNavigation.setNavigationItemSelectedListener { menuItem ->
            bottomSheetNavigationBehaviour.hide()

            val currentMenuItem = binding.bottomSheetNavigation.checkedItem
            if (viewModel.isItemChanged(menuItem.itemId, currentMenuItem?.itemId)) {
                viewModel.bottomSheetListSelection = menuItem.itemId
                binding.bottomSheetNavigation.setCheckedItem(viewModel.bottomSheetListSelection)

                lifecycleScope.launch {
                    delay(resources.getInteger(R.integer.animation_delay_medium).toLong())
                    setListFragment()
                }
            }

            true
        }

        setupBottomSheetScrim(bottomSheetNavigationBehaviour)
    }

    private fun setupBottomSheetSort() {
        val bottomSheetSortBehaviour = getBottomSheetSort()
        bottomSheetSortBehaviour.hide()
        viewModel.currentSort.observe(viewLifecycleOwner) { sort ->
            viewModel.sortToMenuItemMap[sort]?.let { menuItemId ->
                binding.bottomSheetSort.setCheckedItem(menuItemId)
            }
        }

        binding.bottomSheetSort.setNavigationItemSelectedListener { menuItem ->
            bottomSheetSortBehaviour.hide()

            val currentMenuItem = binding.bottomSheetSort.checkedItem
            if (viewModel.isItemChanged(menuItem.itemId, currentMenuItem?.itemId)) {
                viewModel.setCurrentSort(menuItem.itemId)
            }

            true
        }

        setupBottomSheetScrim(bottomSheetSortBehaviour)
    }

    private fun setupBottomSheetScrim(bottomSheetBehavior: BottomSheetBehavior<NavigationView>) {
        val overrideBackCallback = requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, enabled = false) {
                bottomSheetBehavior.hide()
            }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> enableScrim(overrideBackCallback, bottomSheetBehavior)
                    BottomSheetBehavior.STATE_HIDDEN -> disableScrim(overrideBackCallback)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                setScrimColor(slideOffset)
            }
        })
    }

    private fun getBottomSheetNavigation(): BottomSheetBehavior<NavigationView> {
        return BottomSheetBehavior.from(binding.bottomSheetNavigation)
    }

    private fun getBottomSheetSort(): BottomSheetBehavior<NavigationView> {
        return BottomSheetBehavior.from(binding.bottomSheetSort)
    }

    private fun enableScrim(
        overrideBackCallback: OnBackPressedCallback,
        bottomSheet: BottomSheetBehavior<NavigationView>
    ) {
        binding.scrim.setOnClickListener { bottomSheet.hide() }
        overrideBackCallback.isEnabled = true
    }

    private fun disableScrim(overrideBackCallback: OnBackPressedCallback) {
        binding.scrim.isClickable = false
        overrideBackCallback.isEnabled = false
    }

    private fun setScrimColor(slideOffset: Float) {
        binding.scrim.setBackgroundColor(viewModel.calculateScrimColor(slideOffset))
    }

    private fun navigateToAddEditReminder() {
        val action = ListContainerFragmentDirections.actionListContainerToAddEdit()
        findNavController().navigate(action)
    }

    private fun setListFragment() {
        val fragment = when (viewModel.bottomSheetListSelection) {
            R.id.drawer_all_list -> AllListFragment()
            else -> ActiveListFragment()
        }

        binding.toolbar.title = getString(viewModel.getToolbarTitle())

        childFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragment_container_list, fragment)
            .commit()
    }
}

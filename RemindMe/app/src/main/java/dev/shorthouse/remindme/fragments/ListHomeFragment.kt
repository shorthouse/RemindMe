package dev.shorthouse.remindme.fragments

//import android.os.Build
//import android.os.Bundle
//import android.util.Log
//import android.view.MenuItem
//import android.view.View
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import com.google.android.material.transition.MaterialSharedAxis
//import dagger.hilt.android.AndroidEntryPoint
//import dev.shorthouse.remindme.R
//import dev.shorthouse.remindme.databinding.FragmentHomeListBinding
//import dev.shorthouse.remindme.viewmodel.ListContainerViewModel
//
//@AndroidEntryPoint
//class ListHomeFragment : Fragment() {
//    private val requestPermissionLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//            if (isGranted) {
//                Log.i("Permission: ", "Granted")
//            } else {
//                Log.i("Permission: ", "Denied")
//            }
//        }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        setupSearchView()
//        setupBottomSheetSort()
//        requestNotificationPermission()
//    }
//
//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//        binding.toolbar.menu.findItem(R.id.action_search).collapseActionView()
//    }
//
//
//
//    private fun requestNotificationPermission() {
//        if (Build.VERSION.SDK_INT >= 33) {
//            when {
//                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
//                    showNotificationPermissionRationale()
//                }
//
//                else -> {
//                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//                }
//            }
//        }
//    }
//
//    private fun showNotificationPermissionRationale() {
//        AlertDialog.Builder(requireContext())
//            .setTitle(getString(R.string.notification_rationale_title))
//            .setMessage(getString(R.string.notification_rationale_message))
//            .setPositiveButton(getString(R.string.notification_rationale_positive)) { _, _ ->
//                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//            }
//            .setNegativeButton(getString(R.string.notification_rationale_negative)) { _, _ -> }
//            .show()
//    }
//
//    private fun setupSearchView() {
//        val searchViewItem = binding.toolbar.menu.findItem(R.id.action_search)
//        val searchView = searchViewItem.actionView as SearchView
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextChange(filter: String): Boolean {
//                viewModel.currentFilter.value = filter
//                return true
//            }
//
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                hideKeyboard()
//                return true
//            }
//        })
//
//        val overrideBackCallback =
//            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, false) {
//                searchViewItem.collapseActionView()
//            }
//
//        val searchExpandListener = object : MenuItem.OnActionExpandListener {
//            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
//                binding.bottomAppBar.performHide()
//                binding.addReminderFab.hide()
//                overrideBackCallback.isEnabled = true
//                return true
//            }
//
//            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
//                binding.bottomAppBar.performShow()
//                binding.addReminderFab.show()
//                overrideBackCallback.isEnabled = false
//                return true
//            }
//        }
//
//        searchViewItem?.setOnActionExpandListener(searchExpandListener)
//    }
//
//    private fun setupBottomSheetSort() {
//        val bottomSheetSortBehaviour = getBottomSheetSort()
//        bottomSheetSortBehaviour.hide()
//        viewModel.currentSort.observe(viewLifecycleOwner) { sort ->
//            viewModel.sortToMenuItemMap[sort]?.let { menuItemId ->
//                binding.bottomSheetSort.setCheckedItem(menuItemId)
//            }
//        }
//
//        binding.bottomSheetSort.setNavigationItemSelectedListener { menuItem ->
//            bottomSheetSortBehaviour.hide()
//
//            val currentMenuItem = binding.bottomSheetSort.checkedItem
//            if (viewModel.isItemChanged(menuItem.itemId, currentMenuItem?.itemId)) {
//                viewModel.setCurrentSort(menuItem.itemId)
//            }
//
//            true
//        }
//
//        setupBottomSheetScrim(bottomSheetSortBehaviour)
//    }
//}

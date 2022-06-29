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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.adapter.ActiveReminderListAdapter
import dev.shorthouse.remindme.adapter.AllReminderListAdapter
import dev.shorthouse.remindme.databinding.FragmentActiveReminderListBinding
import dev.shorthouse.remindme.viewmodel.ActiveReminderListViewModel

@AndroidEntryPoint
class ActiveReminderListFragment : Fragment() {
    private lateinit var binding: FragmentActiveReminderListBinding

    private val viewModel: ActiveReminderListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentActiveReminderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ActiveReminderListAdapter()

        viewModel.activeReminders.observe(viewLifecycleOwner) { reminders ->
            adapter.submitList(reminders)
        }

        binding.apply {
            activeReminderRecycler.adapter = adapter

            addReminderFab.setOnClickListener {
                navigateToReminderDetails()
            }

            bottomAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.search -> {
                        Toast.makeText(context, "Search icon clicked!", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
        }

        setupBottomNavigationDrawer()
    }

    private fun changeToActiveReminderAdapter() {
        val adapter = ActiveReminderListAdapter()
        viewModel.activeReminders.observe(viewLifecycleOwner) { reminders ->
            adapter.submitList(reminders)
        }

        binding.activeReminderRecycler.adapter = adapter
    }

    private fun changeToAllReminderAdapter() {
        val adapter = AllReminderListAdapter()
        viewModel.allReminders.observe(viewLifecycleOwner) { reminders ->
            adapter.submitList(reminders)
        }

        binding.activeReminderRecycler.adapter = adapter
    }

    private fun setupBottomNavigationDrawer() {
        binding.apply {
            val bottomSheetBehavior = BottomSheetBehavior.from(navigationView)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            navigationView.setCheckedItem(R.id.drawer_active_reminders)

            bottomAppBar.setNavigationOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }

            navigationView.setNavigationItemSelectedListener { menuItem ->
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                if (menuItem.itemId != navigationView.checkedItem?.itemId) {
                    if (menuItem.itemId == R.id.drawer_active_reminders) {
                        navigationView.setCheckedItem(R.id.drawer_active_reminders)
                        changeToActiveReminderAdapter()
                    } else if (menuItem.itemId == R.id.drawer_all_reminders) {
                        navigationView.setCheckedItem(R.id.drawer_all_reminders)
                        changeToAllReminderAdapter()
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
                        activeReminderRecycler.bringToFront()
                        backButtonCallback.isEnabled = false
                    }
                }
            })

        }
    }

    private fun navigateToReminderDetails() {
        val action = ActiveReminderListFragmentDirections
            .actionActiveRemindersToAddEditReminder()
        findNavController().navigate(action)
    }

//    private fun navigateToAllReminders() {
//        changeAdapter()
////        val action = ActiveReminderListFragmentDirections
////            .actionActiveRemindersToAllReminders()
////        findNavController().navigate(action)
//    }
}

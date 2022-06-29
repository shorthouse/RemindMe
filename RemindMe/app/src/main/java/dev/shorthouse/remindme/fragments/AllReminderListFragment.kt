package dev.shorthouse.remindme.fragments

//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.activity.addCallback
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import com.google.android.material.bottomsheet.BottomSheetBehavior
//import dagger.hilt.android.AndroidEntryPoint
//import dev.shorthouse.remindme.R
//import dev.shorthouse.remindme.adapter.AllReminderListAdapter
//import dev.shorthouse.remindme.databinding.FragmentAllReminderListBinding
//import dev.shorthouse.remindme.viewmodel.AllReminderListViewModel
//
//@AndroidEntryPoint
//class AllReminderListFragment : Fragment() {
//    private lateinit var binding: FragmentAllReminderListBinding
//
//    private val viewModel: AllReminderListViewModel by viewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentAllReminderListBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val adapter = AllReminderListAdapter()
//
//        viewModel.reminders.observe(this.viewLifecycleOwner) { reminders ->
//            adapter.submitList(reminders)
//        }
//
//        binding.apply {
//            allReminderRecycler.adapter = adapter
//
//            addReminderFab.setOnClickListener {
//                navigateToReminderDetails()
//            }
//
//            bottomAppBar.setOnMenuItemClickListener { menuItem ->
//                when (menuItem.itemId) {
//                    R.id.search -> {
//                        Toast.makeText(context, "Search icon clicked!", Toast.LENGTH_SHORT).show()
//                        true
//                    }
//                    else -> false
//                }
//            }
//        }
//
//        setupBottomNavigationDrawer()
//    }
//
//    private fun setupBottomNavigationDrawer() {
//        binding.apply {
//            val bottomSheetBehavior = BottomSheetBehavior.from(navigationView)
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//
//            navigationView.setCheckedItem(R.id.drawer_all_reminders)
//
//            bottomAppBar.setNavigationOnClickListener {
//                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//            }
//
//            navigationView.setNavigationItemSelectedListener { menuItem ->
//                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//                if (menuItem.itemId == R.id.drawer_active_reminders) {
//                    navigateToActiveReminders()
//                }
//                true
//            }
//
//            val backButtonCallback =
//                requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
//                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
//                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//                    }
//                }
//
//            bottomSheetBehavior.addBottomSheetCallback(object :
//                BottomSheetBehavior.BottomSheetCallback() {
//                override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                    scrim.setBackgroundColor(viewModel.getScrimBackgroundColour(slideOffset))
//                }
//
//                override fun onStateChanged(bottomSheet: View, newState: Int) {
//                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
//                        scrim.setOnClickListener {
//                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//                        }
//                        scrim.bringToFront()
//                        backButtonCallback.isEnabled = true
//                    } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
//                        scrim.setOnClickListener(null)
//                        allReminderRecycler.bringToFront()
//                        backButtonCallback.isEnabled = false
//                    }
//                }
//            })
//        }
//    }
//
//    private fun navigateToReminderDetails() {
//        val action = AllReminderListFragmentDirections
//            .actionAllRemindersToAddEditReminder()
//        findNavController().navigate(action)
//    }
//
//    private fun navigateToActiveReminders() {
//        val action = AllReminderListFragmentDirections
//            .actionAllRemindersToActiveReminders()
//        findNavController().navigate(action)
//    }
//}

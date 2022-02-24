package dev.shorthouse.habitbuilder.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dev.shorthouse.habitbuilder.BaseApplication
import dev.shorthouse.habitbuilder.databinding.FragmentActiveReminderListBinding
import dev.shorthouse.habitbuilder.ui.adapter.ReminderListAdapter
import dev.shorthouse.habitbuilder.ui.viewmodel.ActiveReminderListViewModel
import dev.shorthouse.habitbuilder.ui.viewmodel.ActiveReminderListViewModelFactory

class ActiveReminderListFragment : Fragment() {
    private var _binding: FragmentActiveReminderListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ActiveReminderListViewModel by activityViewModels {
        ActiveReminderListViewModelFactory(
            (activity?.application as BaseApplication).database.reminderDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentActiveReminderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ReminderListAdapter { reminder ->
            Toast.makeText(context, "Reminder clicked!", Toast.LENGTH_SHORT).show()

            /*
            val action = ActiveReminderListFragmentDirections
                .actionForageableListFragmentToForageableDetailFragment(reminder.id)

            findNavController().navigate(action)
             */
        }

        viewModel.reminders.observe(this.viewLifecycleOwner) {reminders ->
            adapter.submitList(reminders)
        }

        binding.apply {
            activeHabitRecycler.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
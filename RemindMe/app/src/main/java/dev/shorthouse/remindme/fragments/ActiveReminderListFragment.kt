package dev.shorthouse.remindme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.adapter.ActiveReminderListAdapter
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

        viewModel.getActiveReminders().observe(this.viewLifecycleOwner) { reminders ->
            adapter.submitList(reminders)
        }

        binding.apply {
            activeReminderRecycler.adapter = adapter

            addReminderFab.setOnClickListener {
                val action = ActiveReminderListFragmentDirections
                    .actionActiveRemindersToAddEditReminder()
                findNavController().navigate(action)
            }

            activeReminderRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    when {
                        dy > 0 && addReminderFab.isShown -> addReminderFab.hide()
                        dy < 0 && !addReminderFab.isShown -> addReminderFab.show()
                    }
                }
            })
        }
    }
}

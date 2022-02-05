package dev.shorthouse.habitbuilder.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dev.shorthouse.habitbuilder.BaseApplication
import dev.shorthouse.habitbuilder.R
import dev.shorthouse.habitbuilder.databinding.ActiveHabitListFragmentBinding
import dev.shorthouse.habitbuilder.databinding.AddHabitFragmentBinding
import dev.shorthouse.habitbuilder.ui.viewmodel.ActiveHabitListViewModel
import dev.shorthouse.habitbuilder.ui.viewmodel.HabitViewModel
import dev.shorthouse.habitbuilder.ui.viewmodel.HabitViewModelFactory

class ActiveHabitListFragment : Fragment() {
    private var _binding: ActiveHabitListFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HabitViewModel by activityViewModels {
        HabitViewModelFactory(
            (activity?.application as BaseApplication).database.habitDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActiveHabitListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addHabitFab.setOnClickListener {
            findNavController().navigate(R.id.action_activeHabitListFragment_to_addHabitFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
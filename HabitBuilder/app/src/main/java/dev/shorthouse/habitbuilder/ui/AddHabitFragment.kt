package dev.shorthouse.habitbuilder.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import dev.shorthouse.habitbuilder.BaseApplication
import dev.shorthouse.habitbuilder.R
import dev.shorthouse.habitbuilder.ui.viewmodel.HabitViewModel
import dev.shorthouse.habitbuilder.ui.viewmodel.HabitViewModelFactory

class AddHabitFragment : Fragment() {

    private val viewModel: HabitViewModel by activityViewModels {
        HabitViewModelFactory(
            (activity?.application as BaseApplication).database.habitDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_habit_fragment, container, false)
    }
}
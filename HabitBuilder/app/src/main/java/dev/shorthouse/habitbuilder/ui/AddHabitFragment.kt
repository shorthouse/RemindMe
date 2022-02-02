package dev.shorthouse.habitbuilder.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.shorthouse.habitbuilder.R
import dev.shorthouse.habitbuilder.ui.viewmodel.HabitViewModel

class AddHabitFragment : Fragment() {

    companion object {
        fun newInstance() = AddHabitFragment()
    }

    private lateinit var viewModel: HabitViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_habit_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HabitViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
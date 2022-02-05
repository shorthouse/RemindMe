package dev.shorthouse.habitbuilder.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.shorthouse.habitbuilder.R

class ActiveHabitListFragment : Fragment() {

    companion object {
        fun newInstance() = ActiveHabitListFragment()
    }

    private lateinit var viewModel: ActiveHabitListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.active_habit_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ActiveHabitListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
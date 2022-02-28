package dev.shorthouse.habitbuilder.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.shorthouse.habitbuilder.R
import dev.shorthouse.habitbuilder.ui.viewmodel.ReminderDetailsViewModel

class ReminderDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = ReminderDetailsFragment()
    }

    private lateinit var viewModel: ReminderDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reminder_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ReminderDetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
package dev.shorthouse.remindme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.adapter.ReminderListPagerAdapter
import dev.shorthouse.remindme.databinding.FragmentViewPagerBinding
import dev.shorthouse.remindme.utilities.RemindersFilter

@AndroidEntryPoint
class ReminderListViewPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentViewPagerBinding.inflate(inflater, container, false)
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        viewPager.adapter = ReminderListPagerAdapter(this)

        // Set the icon and text for each tab
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.setIcon(getTabIcon(position))
            tab.text = getTabTitle(position)
        }.attach()

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        return binding.root
    }

    private fun getTabIcon(position: Int): Int {
        return when (RemindersFilter.values()[position]) {
            RemindersFilter.ACTIVE_REMINDERS -> R.drawable.ic_calendar_today
            RemindersFilter.ALL_REMINDERS -> R.drawable.ic_calendar_range
        }
    }

    private fun getTabTitle(position: Int): String {
        return when (RemindersFilter.values()[position]) {
            RemindersFilter.ACTIVE_REMINDERS -> getString(R.string.active_reminders)
            RemindersFilter.ALL_REMINDERS -> getString(R.string.all_reminders)
        }
    }
}
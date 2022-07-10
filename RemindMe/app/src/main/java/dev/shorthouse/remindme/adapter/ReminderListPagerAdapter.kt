package dev.shorthouse.remindme.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.shorthouse.remindme.fragments.ReminderListFragment
import dev.shorthouse.remindme.utilities.RemindersFilter

class ReminderListPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val tabFragmentsCreators: Map<RemindersFilter, () -> Fragment> = mapOf(
        RemindersFilter.ACTIVE_REMINDERS to { ReminderListFragment(RemindersFilter.ACTIVE_REMINDERS) },
        RemindersFilter.ALL_REMINDERS to { ReminderListFragment(RemindersFilter.ALL_REMINDERS) }
    )

    override fun getItemCount(): Int {
        return tabFragmentsCreators.size
    }

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[RemindersFilter.values()[position]]?.invoke()
            ?: throw IndexOutOfBoundsException()
    }
}
package dev.shorthouse.remindme.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.shorthouse.remindme.fragments.ActiveReminderListFragment
import dev.shorthouse.remindme.fragments.AllReminderListFragment

const val ACTIVE_REMINDERS_PAGE_INDEX = 0
const val ALL_REMINDERS_PAGE_INDEX = 1

class ReminderListPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        ACTIVE_REMINDERS_PAGE_INDEX to { ActiveReminderListFragment() },
        ALL_REMINDERS_PAGE_INDEX to { AllReminderListFragment() }
    )

    override fun getItemCount(): Int {
        return tabFragmentsCreators.size
    }

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }
}
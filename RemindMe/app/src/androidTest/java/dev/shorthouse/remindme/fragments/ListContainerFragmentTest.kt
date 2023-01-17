package dev.shorthouse.remindme.fragments

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import dev.shorthouse.remindme.FakeDataSource
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.ReminderDataSource
import dev.shorthouse.remindme.di.DataSourceModule
import dev.shorthouse.remindme.launchFragmentInHiltContainer
import dev.shorthouse.remindme.util.TestUtil
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.ZonedDateTime
import javax.inject.Singleton

@HiltAndroidTest
@UninstallModules(DataSourceModule::class)
class ListContainerFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val device: UiDevice
        get() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Module
    @InstallIn(SingletonComponent::class)
    class TestModule {
        @Singleton
        @Provides
        fun provideReminderDataSource(): ReminderDataSource {
            val reminders = mutableListOf(
                TestUtil.createReminder(
                    name = "Test Newer Reminder",
                    startDateTime = ZonedDateTime.parse("2020-01-01T14:02:00Z")
                ),
                TestUtil.createReminder(
                    name = "Test Older Reminder",
                    startDateTime = ZonedDateTime.parse("2000-01-01T08:13:00Z")
                ),
                TestUtil.createReminder(
                    name = "Test Future Reminder",
                    startDateTime = ZonedDateTime.parse("3000-01-01T08:13:00Z")
                )
            )

            return FakeDataSource(reminders)
        }
    }

    @Before
    fun setup() {
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        launchFragmentInHiltContainer<ListHomeFragment>(
            navHostController = navController,
        )
    }

    @Test
    fun when_fragment_created_should_populate_as_expected() {
        navigateToActiveReminders()
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))

        onView(withId(R.id.action_sort)).check(matches(isClickable()))
        onView(withId(R.id.action_search)).check(matches(isClickable()))
        onView(withId(R.id.add_reminder_fab)).check(matches(isClickable()))
        onView(withId(R.id.bottom_app_bar)).check(matches(isDisplayed()))
    }

    @Test
    fun when_search_icon_clicked_should_display_search() {
        navigateToActiveReminders()
        onView(withId(R.id.action_search)).perform(click())

        onView(withId(R.id.search_view)).check(matches(isDisplayed()))
        onView(withHint("Search")).check(matches(isDisplayed()))
    }

    @Test
    fun when_active_reminders_searched_with_some_matching_names_should_display_expected_reminders() {
        navigateToActiveReminders()
        onView(withId(R.id.action_search)).perform(click())
        onView(withId(R.id.search_src_text)).perform(typeText("Newer"))
        device.waitForIdle()

        onView(withText("Test Newer Reminder")).check(matches(isDisplayed()))
        onView(withText("Test Older Reminder")).check(doesNotExist())
        onView(withText("Test Future Reminder")).check(doesNotExist())
    }

    @Test
    fun when_all_reminders_searched_with_some_matching_names_should_display_expected_reminders() {
        navigateToAllReminders()
        onView(withId(R.id.action_search)).perform(click())
        onView(withId(R.id.search_src_text)).perform(typeText("Future"))
        device.waitForIdle()

        onView(withText("Test Future Reminder")).check(matches(isDisplayed()))
        onView(withText("Test Newer Reminder")).check(doesNotExist())
        onView(withText("Test Older Reminder")).check(doesNotExist())
    }

    @Test
    fun when_active_reminders_searched_with_zero_matching_names_should_display_zero_reminders() {
        navigateToActiveReminders()
        onView(withId(R.id.action_search)).perform(click())
        onView(withId(R.id.search_src_text)).perform(typeText("x"))
        device.waitForIdle()

        onView(withText("No reminders found")).check(matches(isDisplayed()))
        onView(withText("Try adjusting your search")).check(matches(isDisplayed()))

        onView(withText("Test Newer Reminder")).check(matches(not(isDisplayed())))
        onView(withText("Test Older Reminder")).check(matches(not(isDisplayed())))
        onView(withText("Test Future Reminder")).check(doesNotExist())
    }

    @Test
    fun when_all_reminders_searched_with_zero_matching_names_should_display_zero_reminders() {
        navigateToAllReminders()
        onView(withId(R.id.action_search)).perform(click())
        onView(withId(R.id.search_src_text)).perform(typeText("x"))
        device.waitForIdle()

        onView(withText("No reminders found")).check(matches(isDisplayed()))
        onView(withText("Try adjusting your search")).check(matches(isDisplayed()))

        onView(withText("Test Newer Reminder")).check(matches(not(isDisplayed())))
        onView(withText("Test Older Reminder")).check(matches(not(isDisplayed())))
        onView(withText("Test Future Reminder")).check(matches(not(isDisplayed())))
    }

    @Test
    fun when_active_reminders_searched_with_all_matching_names_should_display_all_reminders() {
        navigateToActiveReminders()
        onView(withId(R.id.action_search)).perform(click())
        onView(withId(R.id.search_src_text)).perform(typeText("Reminder"))

        onView(withText("Test Newer Reminder")).check(matches(isDisplayed()))
        onView(withText("Test Older Reminder")).check(matches(isDisplayed()))
        onView(withText("Test Future Reminder")).check(doesNotExist())
    }

    @Test
    fun when_all_reminders_searched_with_all_matching_names_should_display_all_reminders() {
        navigateToAllReminders()
        onView(withId(R.id.action_search)).perform(click())
        onView(withId(R.id.search_src_text)).perform(typeText("Reminder"))

        onView(withText("Test Newer Reminder")).check(matches(isDisplayed()))
        onView(withText("Test Older Reminder")).check(matches(isDisplayed()))
        onView(withText("Test Future Reminder")).check(matches(isDisplayed()))
    }

    @Test
    fun when_sort_icon_clicked_should_display_bottom_sheet() {
        navigateToActiveReminders()
        onView(withId(R.id.action_sort)).perform(click())
        device.waitForIdle()

        onView(withId(R.id.bottom_sheet_sort)).check(matches(isDisplayed()))
        onView(withText("Sort by")).check(matches(isDisplayed()))

        onView(withId(R.id.drawer_sort_earliest_date_first)).check(matches(isDisplayed()))
        onView(withText("Earliest date first")).check(matches(isDisplayed()))
        onView(withId(R.id.drawer_sort_latest_date_first)).check(matches(isDisplayed()))
        onView(withText("Latest date first")).check(matches(isDisplayed()))
    }

    @Test
    fun when_sorting_active_reminders_by_earliest_date_first_should_display_reminder_list_sorted_by_newest_first() {
        navigateToActiveReminders()
        onView(withId(R.id.action_sort)).perform(click())
        onView(withId(R.id.drawer_sort_earliest_date_first)).perform(click())

        onView(hasItemAtPosition(hasDescendant(withText("Test Older Reminder")), 0)).check(matches(isDisplayed()))
        onView(hasItemAtPosition(hasDescendant(withText("Test Newer Reminder")), 1)).check(matches(isDisplayed()))
    }

    @Test
    fun when_sorting_active_reminders_by_latest_date_first_should_display_reminder_list_sorted_by_oldest_first() {
        navigateToActiveReminders()
        onView(withId(R.id.action_sort)).perform(click())
        onView(withId(R.id.drawer_sort_latest_date_first)).perform(click())

        onView(hasItemAtPosition(hasDescendant(withText("Test Newer Reminder")), 0)).check(matches(isDisplayed()))
        onView(hasItemAtPosition(hasDescendant(withText("Test Older Reminder")), 1)).check(matches(isDisplayed()))
    }

    @Test
    fun when_sorting_all_reminders_by_newest_first_should_display_reminder_list_sorted_by_newest_first() {
        navigateToAllReminders()
        onView(withId(R.id.action_sort)).perform(click())
        onView(withId(R.id.drawer_sort_earliest_date_first)).perform(click())
        device.waitForIdle()

        onView(hasItemAtPosition(hasDescendant(withText("Test Older Reminder")), 0)).check(matches(isDisplayed()))
        onView(hasItemAtPosition(hasDescendant(withText("Test Newer Reminder")), 1)).check(matches(isDisplayed()))
        onView(hasItemAtPosition(hasDescendant(withText("Test Future Reminder")), 2)).check(matches(isDisplayed()))
    }

    @Test
    fun when_sorting_all_reminders_by_oldest_first_should_display_reminder_list_sorted_by_oldest_first() {
        navigateToAllReminders()
        onView(withId(R.id.action_sort)).perform(click())
        onView(withId(R.id.drawer_sort_latest_date_first)).perform(click())

        onView(hasItemAtPosition(hasDescendant(withText("Test Future Reminder")), 0)).check(matches(isDisplayed()))
        onView(hasItemAtPosition(hasDescendant(withText("Test Newer Reminder")), 1)).check(matches(isDisplayed()))
        onView(hasItemAtPosition(hasDescendant(withText("Test Older Reminder")), 2)).check(matches(isDisplayed()))
    }

    private fun navigateToActiveReminders() {
        onView(withContentDescription("Menu")).perform(click())
        onView(withId(R.id.drawer_active_list)).perform(click())
        device.waitForIdle()
    }

    private fun navigateToAllReminders() {
        onView(withContentDescription("Menu")).perform(click())
        onView(withId(R.id.drawer_all_list)).perform(click())
        device.waitForIdle()
    }

    private fun hasItemAtPosition(matcher: Matcher<View>, position: Int): Matcher<View> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {

            override fun describeTo(description: Description?) {
                description?.appendText("has item at position $position : ")
                matcher.describeTo(description)
            }

            override fun matchesSafely(item: RecyclerView?): Boolean {
                val viewHolder = item?.findViewHolderForAdapterPosition(position)
                return matcher.matches(viewHolder?.itemView)
            }
        }
    }
}

package dev.shorthouse.remindme.fragments

import android.util.Log
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import com.google.android.material.tabs.TabLayout
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
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.ZonedDateTime
import javax.inject.Singleton

@HiltAndroidTest
@UninstallModules(DataSourceModule::class)
class ReminderListViewPagerTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

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

        launchFragmentInHiltContainer<ReminderListViewPagerFragment>(
            navHostController = navController,
        )
    }

    @Test
    fun when_fragment_created_should_populate_as_expected() {
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withText("RemindMe")).check(matches(isDisplayed()))

        onView(withId(R.id.action_sort)).check(matches(isClickable()))
        onView(withId(R.id.action_search)).check(matches(isClickable()))
        onView(withId(R.id.add_reminder_fab)).check(matches(isClickable()))

        onView(withText("Active Reminders")).check(matches(isDisplayed()))
        onView(withText("All Reminders")).check(matches(isDisplayed()))
    }

    @Test
    fun when_active_reminders_tab_clicked_should_display_active_reminders() {
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(0))
        onView(withId(R.id.active_reminder_recycler)).check(matches(isDisplayed()))
    }

    @Test
    fun when_all_reminders_tab_clicked_should_display_all_reminders() {
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(1))
        onView(withId(R.id.all_reminder_recycler)).check(matches(isDisplayed()))
    }

    @Test
    fun when_search_icon_clicked_should_display_search() {
        onView(withId(R.id.action_search)).perform(click())

        onView(withId(R.id.search_view)).check(matches(isDisplayed()))
        onView(withHint("Search")).check(matches(isDisplayed()))
        onView(withText("RemindMe")).check(doesNotExist())
        onView(withId(R.id.action_sort)).check(doesNotExist())
    }

    @Test
    fun when_reminders_searched_with_some_matching_names_should_display_expected_reminders() {
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(0))
        onView(withId(R.id.action_search)).perform(click())
        onView(withId(R.id.search_src_text)).perform(typeText("Newer"))

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val reminder = device.findObject( UiSelector().text("Test Older Reminder"))
        try {
            reminder.waitUntilGone(3000)

            onView(
                allOf(
                    isDescendantOfA(withId(R.id.active_reminder_recycler)),
                    withText("Test Newer Reminder")
                )
            ).check(matches(isDisplayed()))

            onView(
                allOf(
                    isDescendantOfA(withId(R.id.active_reminder_recycler)),
                    withText("Test Older Reminder")
                )
            ).check(doesNotExist())
        } catch (error: UiObjectNotFoundException) {
            Log.e("ReminderListViewPagerTest", "Exception", error)
        }
    }

    @Test
    fun when_reminders_searched_with_zero_matching_names_should_display_zero_reminders() {
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(0))
        onView(withId(R.id.action_search)).perform(click())
        onView(withId(R.id.search_src_text)).perform(typeText("x"))

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val reminder = device.findObject( UiSelector().text("Test Newer Reminder"))
        try {
            reminder.waitUntilGone(3000)

            onView(
                allOf(
                    isDescendantOfA(withId(R.id.active_reminder_recycler)),
                    withText("Test Newer Reminder")
                )
            ).check(doesNotExist())

            onView(
                allOf(
                    isDescendantOfA(withId(R.id.active_reminder_recycler)),
                    withText("Test Older Reminder")
                )
            ).check(doesNotExist())
        } catch (error: UiObjectNotFoundException) {
            Log.e("ReminderListViewPagerTest", "Exception", error)
        }
    }

    @Test
    fun when_reminders_searched_with_all_matching_names_should_display_all_reminders() {
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(0))
        onView(withId(R.id.action_search)).perform(click())
        onView(withId(R.id.search_src_text)).perform(typeText("Reminder"))

        onView(
            allOf(
                isDescendantOfA(withId(R.id.active_reminder_recycler)),
                withText("Test Newer Reminder")
            )
        ).check(matches(isDisplayed()))

        onView(
            allOf(
                isDescendantOfA(withId(R.id.active_reminder_recycler)),
                withText("Test Older Reminder")
            )
        ).check(matches(isDisplayed()))
    }

    @Test
    fun when_sort_icon_clicked_should_display_bottom_sheet() {
        onView(withId(R.id.action_sort)).perform(click())
        onView(withId(R.id.navigation_view_list_sort)).check(matches(isDisplayed()))
        onView(withText("Sort by")).check(matches(isDisplayed()))

        onView(withId(R.id.drawer_sort_newest_first)).check(matches(isDisplayed()))
        onView(withText("Newest first")).check(matches(isDisplayed()))
        onView(withId(R.id.drawer_sort_oldest_first)).check(matches(isDisplayed()))
        onView(withText("Oldest first")).check(matches(isDisplayed()))
    }

    @Test
    fun when_sorting_active_reminders_by_newest_first_should_display_reminder_list_sorted_by_newest_first() {
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(0))
        onView(withId(R.id.action_sort)).perform(click())
        onView(withId(R.id.drawer_sort_newest_first)).perform(click())

        onView(
            allOf(
                withId(R.id.active_reminder_recycler),
                hasItemAtPosition(hasDescendant(withText("Test Newer Reminder")), 0)
            )
        ).check(matches(isDisplayed()))

        onView(
            allOf(
                withId(R.id.active_reminder_recycler),
                hasItemAtPosition(hasDescendant(withText("Test Older Reminder")), 1)
            )
        ).check(matches(isDisplayed()))
    }

    @Test
    fun when_sorting_active_reminders_by_oldest_first_should_display_reminder_list_sorted_by_oldest_first() {
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(0))
        onView(withId(R.id.action_sort)).perform(click())
        onView(withId(R.id.drawer_sort_oldest_first)).perform(click())

        onView(
            allOf(
                withId(R.id.active_reminder_recycler),
                hasItemAtPosition(hasDescendant(withText("Test Older Reminder")), 0)
            )
        ).check(matches(isDisplayed()))

        onView(
            allOf(
                withId(R.id.active_reminder_recycler),
                hasItemAtPosition(hasDescendant(withText("Test Newer Reminder")), 1)
            )
        ).check(matches(isDisplayed()))
    }

    @Test
    fun when_sorting_all_reminders_by_newest_first_should_display_reminder_list_sorted_by_newest_first() {
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(1))
        onView(withId(R.id.action_sort)).perform(click())
        onView(withId(R.id.drawer_sort_newest_first)).perform(click())

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val reminder = device.findObject( UiSelector().text("Test Older Reminder"))
        try {
            reminder.waitForExists(3000)

            onView(
                allOf(
                    withId(R.id.all_reminder_recycler),
                    hasItemAtPosition(hasDescendant(withText("Test Newer Reminder")), 0)
                )
            ).check(matches(isDisplayed()))

            onView(
                allOf(
                    withId(R.id.all_reminder_recycler),
                    hasItemAtPosition(hasDescendant(withText("Test Older Reminder")), 1)
                )
            ).check(matches(isDisplayed()))
        } catch (error: UiObjectNotFoundException) {
            Log.e("ReminderListViewPagerTest", "Exception", error)
        }
    }

    @Test
    fun when_sorting_all_reminders_by_oldest_first_should_display_reminder_list_sorted_by_oldest_first() {
        onView(withId(R.id.tab_layout)).perform(selectTabAtPosition(1))
        onView(withId(R.id.action_sort)).perform(click())
        onView(withId(R.id.drawer_sort_oldest_first)).perform(click())

        onView(
            allOf(
                withId(R.id.all_reminder_recycler),
                hasItemAtPosition(hasDescendant(withText("Test Older Reminder")), 0)
            )
        ).check(matches(isDisplayed()))

        onView(
            allOf(
                withId(R.id.all_reminder_recycler),
                hasItemAtPosition(hasDescendant(withText("Test Newer Reminder")), 1)
            )
        ).check(matches(isDisplayed()))
    }

    private fun hasItemAtPosition(matcher: Matcher<View>, position: Int) : Matcher<View> {
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

    private fun selectTabAtPosition(tabIndex: Int): ViewAction {
        return object : ViewAction {
            override fun perform(uiController: UiController, view: View) {
                val tabLayout = view as TabLayout
                val tabAtIndex: TabLayout.Tab = tabLayout.getTabAt(tabIndex)
                    ?: throw PerformException.Builder()
                        .withCause(Throwable("No tab at index $tabIndex"))
                        .build()

                tabAtIndex.select()
            }

            override fun getDescription() = "with tab at index $tabIndex"

            override fun getConstraints() = allOf(isDisplayed(), isAssignableFrom(TabLayout::class.java))
        }
    }
}
package dev.shorthouse.remindme.fragments

import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import dev.shorthouse.remindme.FakeDataSource
import dev.shorthouse.remindme.data.ReminderDataSource
import dev.shorthouse.remindme.data.RepeatInterval
import dev.shorthouse.remindme.di.DataSourceModule
import dev.shorthouse.remindme.launchFragmentInHiltContainer
import dev.shorthouse.remindme.util.TestUtil
import dev.shorthouse.remindme.R
import org.hamcrest.core.AllOf.allOf
import org.hamcrest.core.IsNot.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Singleton

@UninstallModules(DataSourceModule::class)
@HiltAndroidTest
class ReminderDetailsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var navHostController: TestNavHostController

    @Module
    @InstallIn(SingletonComponent::class)
    class TestModule {
        @Singleton
        @Provides
        fun provideReminderDataSource(): ReminderDataSource {
            val reminders = mutableListOf(
                TestUtil.createReminder(
                    id = 1L,
                    name = "Test Reminder Details",
                    startDateTime = ZonedDateTime.parse("2000-01-01T14:02:00Z")
                ),
                TestUtil.createReminder(
                    id = 2L,
                    name = "Test Reminder Details with Repeat Interval",
                    startDateTime = ZonedDateTime.parse("2000-01-01T14:02:00Z"),
                    repeatInterval = RepeatInterval(2, ChronoUnit.WEEKS)
                ),
                TestUtil.createReminder(
                    id = 3L,
                    name = "Test Reminder Details with Notes",
                    notes = "notes"
                ),
                TestUtil.createReminder(
                    id = 4L,
                    name = "Test Reminder Details with Notification Enabled",
                    isNotificationSent = true
                ),
                TestUtil.createReminder(
                    id = 5L,
                    name = "Test Reminder Details with Everything",
                    repeatInterval = RepeatInterval(2, ChronoUnit.WEEKS),
                    isNotificationSent = true,
                    notes = "notes"
                ),
            )
            return FakeDataSource(reminders)
        }
    }

    @Before
    fun setup() {
        navHostController = TestNavHostController(ApplicationProvider.getApplicationContext())
    }

    // Test case of massive reminder name
    // Check is fully and properly displayed
    // Also check the limit on this, should have a limit

    // TODO
    // same for notes, do mega notes
    // check this has a limit cos i could just put a massive amount in
    // check if goes off screen that it can scroll down
    // TODO currently doesn't scroll so isn't visible, make failing test for this

    @Test
    fun when_reminder_details_fragment_created_should_display_toolbar() {
        val navigationArgs = ReminderDetailsFragmentArgs(id = 1L).toBundle()

        launchFragmentInHiltContainer<ReminderDetailsFragment>(
            navHostController = navHostController,
            fragmentArgs = navigationArgs
        )

        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.action_delete)).check(
            matches(
                allOf(
                    isClickable(), isDisplayed()
                )
            )
        )
    }

    @Test
    fun when_reminder_details_fragment_created_should_display_content() {
        val navigationArgs = ReminderDetailsFragmentArgs(id = 1L).toBundle()

        launchFragmentInHiltContainer<ReminderDetailsFragment>(
            navHostController = navHostController,
            fragmentArgs = navigationArgs
        )

        onView(withId(R.id.name)).check(matches(isDisplayed()))
        onView(withId(R.id.ic_calendar)).check(matches(isDisplayed()))
        onView(withId(R.id.start_date)).check(matches(isDisplayed()))
        onView(withId(R.id.divider_start_date)).check(matches(isDisplayed()))
        onView(withId(R.id.ic_clock)).check(matches(isDisplayed()))
        onView(withId(R.id.start_time)).check(matches(isDisplayed()))

        onView(withId(R.id.divider_start_time)).check(matches(not(isDisplayed())))


        onView(withId(R.id.name)).check(matches(withText("Test Reminder Details")))
        onView(withId(R.id.notes)).check(matches(withText("")))
//        onView(withText("Test Reminder Details")).check(matches(isDisplayed()))
//        onView(withText("01 Jan 2000"))
    }
}
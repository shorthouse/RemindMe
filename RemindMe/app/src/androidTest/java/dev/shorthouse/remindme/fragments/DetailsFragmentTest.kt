package dev.shorthouse.remindme.fragments

import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
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
import dev.shorthouse.remindme.model.RepeatInterval
import dev.shorthouse.remindme.di.DataSourceModule
import dev.shorthouse.remindme.launchFragmentInHiltContainer
import dev.shorthouse.remindme.util.TestUtil
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
class DetailsFragmentTest {

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
                    startDateTime = ZonedDateTime.parse("2000-01-01T14:02:00Z"),
                    repeatInterval = RepeatInterval(2, ChronoUnit.WEEKS),
                    isNotificationSent = true,
                    notes = "notes"
                ),
                TestUtil.createReminder(
                    id = 6L,
                    name = "m".repeat(200)
                ),
                TestUtil.createReminder(
                    id = 7L,
                    name = "Test Reminder Large Notes",
                    notes = "m".repeat(1000),
                    isNotificationSent = true
                ),
            )
            return FakeDataSource(reminders)
        }
    }

    @Before
    fun setup() {
        navHostController = TestNavHostController(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun when_reminder_details_fragment_created_should_display_toolbar() {
        val navigationArgs = DetailsFragmentArgs(id = 1L).toBundle()

        launchFragmentInHiltContainer<DetailsFragment>(
            navHostController = navHostController,
            fragmentArgs = navigationArgs
        )

        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId((R.id.action_edit))).check(matches(allOf(isClickable(), isDisplayed())))

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().context)
        onView(withText("Complete")).check(matches(isDisplayed()))
        onView(withText("Delete")).check(matches(isDisplayed()))
    }

    @Test
    fun when_reminder_details_fragment_created_should_display_content() {
        val navigationArgs = DetailsFragmentArgs(id = 1L).toBundle()

        launchFragmentInHiltContainer<DetailsFragment>(
            navHostController = navHostController,
            fragmentArgs = navigationArgs
        )

        onView(withId(R.id.name)).check(matches(isDisplayed()))
        onView(withId(R.id.ic_calendar)).check(matches(isDisplayed()))
        onView(withId(R.id.start_date)).check(matches(isDisplayed()))
        onView(withId(R.id.ic_clock)).check(matches(isDisplayed()))
        onView(withId(R.id.start_time)).check(matches(isDisplayed()))

        onView(withId(R.id.ic_repeat)).check(matches(not(isDisplayed())))
        onView(withId(R.id.repeat_interval)).check(matches(not(isDisplayed())))
        onView(withId(R.id.ic_notes)).check(matches(not(isDisplayed())))
        onView(withId(R.id.notes)).check(matches(not(isDisplayed())))
        onView(withId(R.id.ic_notification)).check(matches(not(isDisplayed())))
        onView(withId(R.id.notification)).check(matches(not(isDisplayed())))

        onView(withId(R.id.name)).check(matches(withText("Test Reminder Details")))
        onView(withId(R.id.start_date)).check(matches(withText("Sat, 01 Jan 2000")))
        onView(withId(R.id.start_time)).check(matches(withText("14:02")))
    }

    @Test
    fun when_reminder_has_repeat_interval_should_display_repeat_interval_section() {
        val navigationArgs = DetailsFragmentArgs(id = 2L).toBundle()

        launchFragmentInHiltContainer<DetailsFragment>(
            navHostController = navHostController,
            fragmentArgs = navigationArgs
        )

        onView(withId(R.id.ic_repeat)).check(matches(isDisplayed()))
        onView(withId(R.id.repeat_interval)).check(matches(isDisplayed()))

        onView(withId(R.id.ic_notes)).check(matches(not(isDisplayed())))
        onView(withId(R.id.notes)).check(matches(not(isDisplayed())))
        onView(withId(R.id.ic_notification)).check(matches(not(isDisplayed())))
        onView(withId(R.id.notification)).check(matches(not(isDisplayed())))

        onView(withId(R.id.name)).check(matches(withText("Test Reminder Details with Repeat Interval")))
        onView(withId(R.id.repeat_interval)).check(matches(withText("2 weeks")))
    }

    @Test
    fun when_reminder_has_notes_should_display_notes_section() {
        val navigationArgs = DetailsFragmentArgs(id = 3L).toBundle()

        launchFragmentInHiltContainer<DetailsFragment>(
            navHostController = navHostController,
            fragmentArgs = navigationArgs
        )

        onView(withId(R.id.ic_notes)).check(matches(isDisplayed()))
        onView(withId(R.id.notes)).check(matches(isDisplayed()))

        onView(withId(R.id.ic_repeat)).check(matches(not(isDisplayed())))
        onView(withId(R.id.repeat_interval)).check(matches(not(isDisplayed())))
        onView(withId(R.id.ic_notification)).check(matches(not(isDisplayed())))
        onView(withId(R.id.notification)).check(matches(not(isDisplayed())))

        onView(withId(R.id.name)).check(matches(withText("Test Reminder Details with Notes")))
        onView(withId(R.id.notes)).check(matches(withText("notes")))
    }

    @Test
    fun when_reminder_has_notification_enabled_should_display_notification_section() {
        val navigationArgs = DetailsFragmentArgs(id = 4L).toBundle()

        launchFragmentInHiltContainer<DetailsFragment>(
            navHostController = navHostController,
            fragmentArgs = navigationArgs
        )

        onView(withId(R.id.ic_notification)).check(matches(isDisplayed()))
        onView(withId(R.id.notification)).check(matches(isDisplayed()))

        onView(withId(R.id.ic_repeat)).check(matches(not(isDisplayed())))
        onView(withId(R.id.repeat_interval)).check(matches(not(isDisplayed())))
        onView(withId(R.id.ic_notes)).check(matches(not(isDisplayed())))
        onView(withId(R.id.notes)).check(matches(not(isDisplayed())))

        onView(withId(R.id.name)).check(matches(withText("Test Reminder Details with Notification Enabled")))
        onView(withId(R.id.notification)).check(matches(withText("Notifications enabled")))
    }

    @Test
    fun when_reminder_has_all_optional_parts_enabled_should_display_all_optional_parts() {
        val navigationArgs = DetailsFragmentArgs(id = 5L).toBundle()

        launchFragmentInHiltContainer<DetailsFragment>(
            navHostController = navHostController,
            fragmentArgs = navigationArgs
        )

        onView(withId(R.id.name)).check(matches(isDisplayed()))
        onView(withId(R.id.ic_calendar)).check(matches(isDisplayed()))
        onView(withId(R.id.start_date)).check(matches(isDisplayed()))

        onView(withId(R.id.ic_clock)).check(matches(isDisplayed()))
        onView(withId(R.id.start_time)).check(matches(isDisplayed()))

        onView(withId(R.id.ic_repeat)).check(matches(isDisplayed()))
        onView(withId(R.id.repeat_interval)).check(matches(isDisplayed()))

        onView(withId(R.id.ic_notes)).check(matches(isDisplayed()))
        onView(withId(R.id.notes)).check(matches(isDisplayed()))

        onView(withId(R.id.ic_notification)).check(matches(isDisplayed()))
        onView(withId(R.id.notification)).check(matches(isDisplayed()))

        onView(withId(R.id.name)).check(matches(withText("Test Reminder Details with Everything")))
        onView(withId(R.id.start_date)).check(matches(withText("Sat, 01 Jan 2000")))
        onView(withId(R.id.start_time)).check(matches(withText("14:02")))
        onView(withId(R.id.repeat_interval)).check(matches(withText("2 weeks")))
        onView(withId(R.id.notes)).check(matches(withText("notes")))
        onView(withId(R.id.notification)).check(matches(withText("Notifications enabled")))
    }

    @Test
    fun when_reminder_with_largest_possible_name_should_display_correctly() {
        val navigationArgs = DetailsFragmentArgs(id = 6L).toBundle()

        launchFragmentInHiltContainer<DetailsFragment>(
            navHostController = navHostController,
            fragmentArgs = navigationArgs
        )

        onView(withId(R.id.name)).check(matches(isCompletelyDisplayed()))
        onView(withText("m".repeat(200))).check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun when_reminder_with_largest_possible_notes_should_display_correctly() {
        val navigationArgs = DetailsFragmentArgs(id = 7L).toBundle()

        launchFragmentInHiltContainer<DetailsFragment>(
            navHostController = navHostController,
            fragmentArgs = navigationArgs
        )

        onView(withId(R.id.ic_notification)).perform(scrollTo())
        onView(withId(R.id.ic_notification)).check(matches(isCompletelyDisplayed()))
        onView(withId(R.id.notification)).check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun when_delete_icon_clicked_should_show_alert_dialog() {
        val navigationArgs = DetailsFragmentArgs(id = 1L).toBundle()

        launchFragmentInHiltContainer<DetailsFragment>(
            navHostController = navHostController,
            fragmentArgs = navigationArgs
        )

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().context)
        onView(withText("Delete")).perform(click())

        onView(withText("Delete this reminder?")).check(matches(isDisplayed()))
        onView(withText("Cancel")).check(matches(isDisplayed()))
        onView(withText("Delete")).check(matches(isDisplayed()))
    }

    @Test
    fun when_delete_dialog_cancel_button_clicked_should_dismiss_delete_dialog() {
        val navigationArgs = DetailsFragmentArgs(id = 1L).toBundle()

        launchFragmentInHiltContainer<DetailsFragment>(
            navHostController = navHostController,
            fragmentArgs = navigationArgs
        )

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().context)
        onView(withText("Delete")).perform(click())
        onView(withText("Cancel")).perform(click())

        onView(withText("Delete this reminder?")).check(doesNotExist())
        onView(withText("Cancel")).check(doesNotExist())
        onView(withText("Delete")).check(doesNotExist())
    }

    @Test
    fun when_delete_dialog_delete_button_clicked_should_dismiss_delete_dialog_and_show_toast() {
        val navigationArgs = DetailsFragmentArgs(id = 1L).toBundle()

        launchFragmentInHiltContainer<DetailsFragment>(
            navHostController = navHostController,
            fragmentArgs = navigationArgs
        )

        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().context)
        onView(withText("Delete")).perform(click())
        onView(withText("Delete")).perform(click())

        onView(withText("Delete this reminder?")).check(doesNotExist())
        onView(withText("Cancel")).check(doesNotExist())
        onView(withText("Delete")).check(doesNotExist())
    }
}

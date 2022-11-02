package dev.shorthouse.remindme.fragments

import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
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
import dev.shorthouse.remindme.util.setTextInTextView
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Singleton

@UninstallModules(DataSourceModule::class)
@HiltAndroidTest
class EditFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Module
    @InstallIn(SingletonComponent::class)
    class TestModule {
        @Singleton
        @Provides
        fun provideReminderDataSource(): ReminderDataSource {
            val reminders = mutableListOf(
                TestUtil.createReminder(
                    id = 1L,
                    name = "Test Edit Reminder",
                    startDateTime = ZonedDateTime.parse("3000-01-01T14:02:00Z"),
                    notes = "Reminder edit notes",
                    isNotificationSent = true,
                    repeatInterval = RepeatInterval(3, ChronoUnit.WEEKS),
                )
            )

            return FakeDataSource(reminders)
        }
    }

    @Before
    fun setup() {
        val navHostController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        val navigationArgs = EditFragmentArgs(id = 1L).toBundle()

        launchFragmentInHiltContainer<EditFragment>(
            navHostController = navHostController,
            fragmentArgs = navigationArgs
        )
    }

    @Test
    fun when_edit_reminder_fragment_created_should_display_toolbar() {
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.action_save)).check(matches(isDisplayed()))
    }

    @Test
    fun when_edit_reminder_fragment_created_should_display_content() {
        onView(withId(R.id.name_label)).check(matches(isDisplayed()))
        onView(withId(R.id.start_date_icon)).check(matches(isDisplayed()))
        onView(withId(R.id.start_date_label)).check(matches(isDisplayed()))
        onView(withId(R.id.start_time_icon)).check(matches(isDisplayed()))
        onView(withId(R.id.start_time_label)).check(matches(isDisplayed()))
        onView(withId(R.id.start_time_icon)).check(matches(isDisplayed()))
        onView(withId(R.id.notes_icon)).check(matches(isDisplayed()))
        onView(withId(R.id.notes_label)).check(matches(isDisplayed()))
        onView(withId(R.id.notification_icon)).check(matches(isDisplayed()))
        onView(withId(R.id.notification_header)).check(matches(isDisplayed()))
        onView(withId(R.id.notification_switch)).check(matches(isDisplayed()))
        onView(withId(R.id.repeat_icon)).check(matches(isDisplayed()))
        onView(withId(R.id.repeat_header)).check(matches(isDisplayed()))
        onView(withId(R.id.repeat_switch)).check(matches(isDisplayed()))
    }

    @Test
    fun when_edit_reminder_fragment_created_should_display_expected_reminder_data() {
        onView(withId(R.id.name_input)).check(matches(withText("Test Edit Reminder")))
        onView(withId(R.id.start_date_input)).check(matches(withText("Wed, 01 Jan 3000")))
        onView(withId(R.id.start_time_input)).check(matches(withText("14:02")))
        onView(withId(R.id.notes_input)).check(matches(withText("Reminder edit notes")))
        onView(withId(R.id.notification_switch)).check(matches(isChecked()))
        onView(withId(R.id.repeat_switch)).check(matches(isChecked()))
        onView(withId(R.id.repeats_every_header)).check(matches(isDisplayed()))
        onView(withId(R.id.repeat_value_input)).check(matches(withText("3")))
    }

    @Test
    fun when_repeat_value_changed_to_singular_should_display_singular_repeat_unit() {
        onView(withId(R.id.repeat_value_input)).perform(replaceText("1"))
    }

    @Test
    fun when_repeat_value_changed_to_plural_should_display_plural_repeat_unit() {
        onView(withId(R.id.repeat_value_input)).perform(replaceText("2"))
        onView(withId(R.id.repeat_unit_input)).check(matches(withText("weeks")))
    }

    @Test
    fun when_repeat_value_changed_to_empty_should_display_singular_repeat_unit() {
        onView(withId(R.id.repeat_value_input)).perform(replaceText(""))
        onView(withId(R.id.repeat_unit_input)).check(matches(withText("weeks")))
    }

    @Test
    fun when_large_notes_input_should_expand_to_scrollable_view() {
        onView(withId(R.id.notes_input)).perform(setTextInTextView("m".repeat(1000)))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.add_edit_scroll_view)).perform(ViewActions.swipeUp())
        onView(withId(R.id.repeat_header)).check(matches(isDisplayed()))
    }
}

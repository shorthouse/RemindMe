package dev.shorthouse.remindme.fragments

import android.view.View
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.launchFragmentInHiltContainer
import dev.shorthouse.remindme.util.checkToastDisplayed
import dev.shorthouse.remindme.util.setTextInTextView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
@HiltAndroidTest
class AddEditFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var decorView: View

    @Before
    fun setup() {
        val navigationArgs = AddEditFragmentArgs(isEditReminder = false).toBundle()

        val navHostController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        launchFragmentInHiltContainer<AddEditFragment>(
            navHostController = navHostController,
            fragmentArgs = navigationArgs,
        ) {
            this.activity?.window?.decorView?.let {
                decorView = it
            }
        }

        Espresso.closeSoftKeyboard()
    }

    @Test
    fun when_add_reminder_fragment_created_should_display_toolbar() {
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withContentDescription("Close")).check(matches(isClickable()))
        onView(withId(R.id.save_reminder)).check(matches(isClickable()))
    }

    @Test
    fun when_add_reminder_fragment_created_should_display_content() {
        onView(withId(R.id.name_label)).check(matches(isDisplayed()))
        onView(withId(R.id.start_date_label)).check(matches(isDisplayed()))
        onView(withId(R.id.start_time_label)).check(matches(isDisplayed()))
        onView(withId(R.id.notes_label)).check(matches(isDisplayed()))
        onView(withId(R.id.notification_header)).check(matches(isDisplayed()))
        onView(withId(R.id.notification_switch)).check(matches(isDisplayed()))
        onView(withId(R.id.repeat_header)).check(matches(isDisplayed()))
        onView(withId(R.id.repeat_switch)).check(matches(isDisplayed()))
    }

    @Test
    fun when_add_reminder_fragment_created_should_display_hints() {
        onView(withId(R.id.name_input)).check(matches(withHint("Name")))
        onView(withId(R.id.start_date_input)).check(matches(withHint("Reminder start date")))
        onView(withId(R.id.start_time_input)).check(matches(withHint("Reminder start time")))
        onView(withId(R.id.notes_input)).check(matches(withHint("Notes")))
    }

    @Test
    fun when_add_reminder_fragment_created_should_focus_on_reminder_name() {
        onView(withId(R.id.name_input)).check(matches(isFocused()))
    }

    @Test
    fun when_saving_reminder_with_time_in_the_past_should_display_error_toast() {
        onView(withId(R.id.name_input)).perform(typeText("Reminder name"))
        onView(withId(R.id.start_date_input)).perform(setTextInTextView("Sat, 01 Jan 2000"))
        onView(withId(R.id.start_time_input)).perform(setTextInTextView("00:00"))
        onView(withId(R.id.save_reminder)).perform(click())
        checkToastDisplayed("The start time cannot be in the past.", decorView)
    }

    @Test
    fun when_enable_repeat_reminder_toggle_should_display_repeat_dropdown() {
        onView(withId(R.id.repeat_switch)).perform(click())
        onView(withId(R.id.repeats_every_header)).check(matches(withText("Repeats every")))
        onView(withId(R.id.repeat_value_label)).check(matches(isDisplayed()))
        onView(withId(R.id.repeat_value_input)).check(matches(withText("1")))
        onView(withId(R.id.repeat_unit_label)).check(matches(isDisplayed()))
        onView(withId(R.id.repeat_unit_input)).check(matches(withText("day")))
    }

    @Test
    fun when_repeat_value_changed_to_singular_should_display_singular_repeat_unit() {
        onView(withId(R.id.repeat_switch)).perform(click())
        onView(withId(R.id.repeat_value_input)).perform(replaceText("1"))
        onView(withId(R.id.repeat_unit_input)).check(matches(withText("day")))
    }

    @Test
    fun when_repeat_value_changed_to_plural_should_display_plural_repeat_unit() {
        onView(withId(R.id.repeat_switch)).perform(click())
        onView(withId(R.id.repeat_value_input)).perform(replaceText("2"))
        onView(withId(R.id.repeat_unit_input)).check(matches(withText("days")))
    }

    @Test
    fun when_repeat_value_changed_to_empty_should_display_singular_repeat_unit() {
        onView(withId(R.id.repeat_switch)).perform(click())
        onView(withId(R.id.repeat_value_input)).perform(replaceText(""))
        onView(withId(R.id.repeat_unit_input)).check(matches(withText("days")))
    }

    @Test
    fun when_saving_reminder_with_empty_repeat_interval_should_display_error_toast() {
        onView(withId(R.id.name_input)).perform(typeText("Reminder name"))
        onView(withId(R.id.repeat_switch)).perform(click())
        onView(withId(R.id.repeat_value_input)).perform(replaceText(""))
        onView(withId(R.id.save_reminder)).perform(click())
        checkToastDisplayed("The repeat interval cannot be empty.", decorView)
    }

    @Test
    fun when_saving_reminder_with_zero_repeat_interval_should_display_error_toast() {
        onView(withId(R.id.name_input)).perform(typeText("Reminder name"))
        onView(withId(R.id.repeat_switch)).perform(click())
        onView(withId(R.id.repeat_value_input)).perform(replaceText("0"))
        onView(withId(R.id.save_reminder)).perform(click())
        checkToastDisplayed("The repeat interval cannot be empty.", decorView)
    }

    @Test
    fun when_valid_reminder_entered_should_display_toast() {
        onView(withId(R.id.name_input)).perform(typeText("Reminder name"))
        onView(withId(R.id.save_reminder)).perform(click())
        checkToastDisplayed("Reminder saved", decorView)
    }

    @Test
    fun when_large_notes_input_should_expand_to_scrollable_view() {
        onView(withId(R.id.notes_input)).perform(setTextInTextView("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum"))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.add_edit_scroll_view)).perform(swipeUp())
        onView(withId(R.id.repeat_header)).check(matches(isDisplayed()))
    }
}


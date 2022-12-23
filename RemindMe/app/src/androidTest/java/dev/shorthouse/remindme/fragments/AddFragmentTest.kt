//package dev.shorthouse.remindme.fragments
//
//import androidx.navigation.testing.TestNavHostController
//import androidx.test.core.app.ApplicationProvider
//import androidx.test.espresso.Espresso
//import androidx.test.espresso.Espresso.onView
//import androidx.test.espresso.action.ViewActions.*
//import androidx.test.espresso.assertion.ViewAssertions.matches
//import androidx.test.espresso.matcher.ViewMatchers.*
//import androidx.test.platform.app.InstrumentationRegistry
//import androidx.test.uiautomator.UiDevice
//import dagger.hilt.android.testing.HiltAndroidRule
//import dagger.hilt.android.testing.HiltAndroidTest
//import dev.shorthouse.remindme.R
//import dev.shorthouse.remindme.launchFragmentInHiltContainer
//import dev.shorthouse.remindme.util.setTextInTextView
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import org.hamcrest.Matchers.allOf
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//
//@ExperimentalCoroutinesApi
//@HiltAndroidTest
//class AddFragmentTest {
//
//    @get:Rule
//    var hiltRule = HiltAndroidRule(this)
//
//    @Before
//    fun setup() {
//        val navHostController = TestNavHostController(
//            ApplicationProvider.getApplicationContext()
//        )
//
//        launchFragmentInHiltContainer<AddFragment>(navHostController = navHostController)
//
//        Espresso.closeSoftKeyboard()
//    }
//
//    @Test
//    fun when_add_reminder_fragment_created_should_display_toolbar() {
//        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
//        onView(withId(R.id.action_save)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun when_add_reminder_fragment_created_should_display_content() {
//        onView(withId(R.id.name_label)).check(matches(isDisplayed()))
//        onView(withId(R.id.start_date_icon)).check(matches(isDisplayed()))
//        onView(withId(R.id.start_date_label)).check(matches(isDisplayed()))
//        onView(withId(R.id.start_time_icon)).check(matches(isDisplayed()))
//        onView(withId(R.id.start_time_label)).check(matches(isDisplayed()))
//        onView(withId(R.id.start_time_icon)).check(matches(isDisplayed()))
//        onView(withId(R.id.notes_icon)).check(matches(isDisplayed()))
//        onView(withId(R.id.notes_label)).check(matches(isDisplayed()))
//        onView(withId(R.id.notification_icon)).check(matches(isDisplayed()))
//        onView(withId(R.id.notification_header)).check(matches(isDisplayed()))
//        onView(withId(R.id.notification_switch)).check(matches(isDisplayed()))
//        onView(withId(R.id.repeat_icon)).check(matches(isDisplayed()))
//        onView(withId(R.id.repeat_header)).check(matches(isDisplayed()))
//        onView(withId(R.id.repeat_switch)).check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun when_add_reminder_fragment_created_should_display_hints() {
//        onView(withId(R.id.name_input)).check(matches(withHint("RemindMe to…")))
//        onView(withId(R.id.notes_input)).check(matches(withHint("Add notes")))
//    }
//
//    @Test
//    fun when_add_reminder_fragment_created_should_focus_on_reminder_name() {
//        onView(withId(R.id.name_input)).check(matches(isFocused()))
//    }
//
//    @Test
//    fun when_enable_repeat_reminder_toggle_should_display_repeat_selector() {
//        onView(withId(R.id.repeat_switch)).perform(click())
//        onView(withId(R.id.repeats_every_header)).check(matches(withText("Repeats every")))
//        onView(withId(R.id.repeat_value_label)).check(matches(isDisplayed()))
//        onView(withId(R.id.repeat_value_input)).check(matches(withText("1")))
//        onView(withId(R.id.repeat_unit_radio_group)).check(matches(isDisplayed()))
//        onView(withId(R.id.repeat_unit_days)).check(matches(isDisplayed()))
//        onView(withId(R.id.repeat_unit_weeks)).check(matches(isDisplayed()))
//        onView(withId(R.id.repeat_unit_days)).check(matches(isChecked()))
//        onView(withId(R.id.repeat_unit_weeks)).check(matches(isNotChecked()))
//        onView(allOf(withText("Day"), withParent(withId(R.id.repeat_unit_radio_group))))
//            .check(matches(isDisplayed()))
//        onView(allOf(withText("Week"), withParent(withId(R.id.repeat_unit_radio_group))))
//            .check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun when_repeat_value_changed_to_singular_should_display_singular_repeat_unit() {
//        onView(withId(R.id.repeat_switch)).perform(click())
//        onView(withId(R.id.repeat_value_input)).perform(replaceText("1"))
//        onView(allOf(withText("Day"), withParent(withId(R.id.repeat_unit_radio_group))))
//            .check(matches(isDisplayed()))
//        onView(allOf(withText("Week"), withParent(withId(R.id.repeat_unit_radio_group))))
//            .check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun when_repeat_value_changed_to_plural_should_display_plural_repeat_unit() {
//        onView(withId(R.id.repeat_switch)).perform(click())
//        onView(withId(R.id.repeat_value_input)).perform(replaceText("2"))
//        onView(allOf(withText("Days"), withParent(withId(R.id.repeat_unit_radio_group))))
//            .check(matches(isDisplayed()))
//        onView(allOf(withText("Weeks"), withParent(withId(R.id.repeat_unit_radio_group))))
//            .check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun when_repeat_value_changed_to_empty_should_display_singular_repeat_unit() {
//        onView(withId(R.id.repeat_switch)).perform(click())
//        onView(withId(R.id.repeat_value_input)).perform(replaceText(""))
//        onView(allOf(withText("Days"), withParent(withId(R.id.repeat_unit_radio_group))))
//            .check(matches(isDisplayed()))
//        onView(allOf(withText("Weeks"), withParent(withId(R.id.repeat_unit_radio_group))))
//            .check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun when_large_notes_input_should_expand_to_scrollable_view() {
//        onView(withId(R.id.notes_input)).perform(setTextInTextView("m".repeat(1000)))
//        Espresso.closeSoftKeyboard()
//        onView(withId(R.id.repeat_header)).perform(scrollTo())
//        onView(withId(R.id.repeat_header)).check(matches(isCompletelyDisplayed()))
//    }
//}

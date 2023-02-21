package dev.shorthouse.remindme.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.shorthouse.remindme.HiltTestActivity
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.theme.RemindMeTheme
import dev.shorthouse.remindme.util.ReminderTestUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalTime

@HiltAndroidTest
class ReminderDetailsTest {

    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltTestRule.inject()
    }

    private fun setContent(reminderState: ReminderState) {
        composeTestRule.setContent {
            RemindMeTheme {
//                ReminderDetailsScaffold(
//                    reminderState = reminderState,
//                    onNavigateUp = {},
//                    onEdit = {},
//                    onDelete = {},
//                    onComplete = {}
//                )
            }
        }
    }

    @Test
    fun when_reminder_details_created_should_display_top_app_bar() {
        val reminder = ReminderTestUtil().createReminderState()
        setContent(reminder)

        composeTestRule.apply {
            onNodeWithText("Details").assertIsDisplayed()
            onNodeWithContentDescription("Back").assertIsDisplayed()
            onNodeWithContentDescription("Edit").assertIsDisplayed()
            onNodeWithContentDescription("More").assertIsDisplayed()

            onNodeWithContentDescription("More").performClick()
            onNodeWithText("Complete").assertIsDisplayed()
            onNodeWithText("Delete").assertIsDisplayed()
        }
    }

    @Test
    fun when_completed_reminder_details_created_should_display_top_app_bar() {
        val completedReminder = ReminderTestUtil().createReminderState(isCompleted = true)
        setContent(completedReminder)

        composeTestRule.apply {
            onNodeWithText("Details").assertIsDisplayed()
            onNodeWithContentDescription("Back").assertIsDisplayed()
            onNodeWithContentDescription("Delete").assertIsDisplayed()

            onNodeWithContentDescription("Edit").assertDoesNotExist()
            onNodeWithContentDescription("More").assertDoesNotExist()
        }
    }

    @Test
    fun when_reminder_details_created_should_display_mandatory_details() {
        val baseReminder = ReminderTestUtil().createReminderState(
            name = "Test reminder name",
            date = "Wed, 22 Mar 2000",
            time = LocalTime.of(14, 30)
        )
        setContent(baseReminder)

        composeTestRule.apply {
            onNodeWithText("Test reminder name").assertIsDisplayed()

            onNodeWithContentDescription("Date").assertIsDisplayed()
            onNodeWithText("Wed, 22 Mar 2000").assertIsDisplayed()

            onNodeWithContentDescription("Time").assertIsDisplayed()
            onNodeWithText("14:30").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_has_notification_enabled_should_display_notification_section() {
        val notifyingReminder = ReminderTestUtil().createReminderState(isNotificationSent = true)
        setContent(notifyingReminder)

        composeTestRule.apply {
            onNodeWithContentDescription("Notification").assertIsDisplayed()
            onNodeWithText("Notifications enabled").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_has_repeat_interval_should_display_repeat_interval_section() {
        val repeatReminder = ReminderTestUtil().createReminderState(
            repeatAmount = "2",
            repeatUnit = "Weeks",
            isRepeatReminder = true
        )
        setContent(repeatReminder)

        composeTestRule.apply {
            onNodeWithContentDescription("Repeat Interval").assertIsDisplayed()
            onNodeWithText("2 Weeks").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_has_notes_should_display_notes_section() {
        val notesReminder = ReminderTestUtil().createReminderState(notes = "Reminder notes")
        setContent(notesReminder)

        composeTestRule.apply {
            onNodeWithContentDescription("Notes").assertIsDisplayed()
            onNodeWithText("Reminder notes").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_has_all_optional_parts_enabled_should_display_all_optional_parts() {
        val notifyingRepeatNotesReminder = ReminderTestUtil().createReminderState(
            isNotificationSent = true,
            repeatAmount = "2",
            repeatUnit = "Weeks",
            isRepeatReminder = true,
            notes = "Reminder notes"
        )
        setContent(notifyingRepeatNotesReminder)

        composeTestRule.apply {
            onNodeWithContentDescription("Notification").assertIsDisplayed()
            onNodeWithContentDescription("Repeat Interval").assertIsDisplayed()
            onNodeWithContentDescription("Notes").assertIsDisplayed()

            onNodeWithText("Notifications enabled").assertIsDisplayed()
            onNodeWithText("2 Weeks").assertIsDisplayed()
            onNodeWithText("Reminder notes").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_with_largest_possible_name_should_display_correctly() {
        val longNameReminder = ReminderTestUtil().createReminderState(
            name = "m".repeat(200)
        )
        setContent(longNameReminder)

        composeTestRule.apply {
            onNodeWithText("m".repeat(200)).assertIsDisplayed()
        }
    }

    @Test
    fun when_delete_overflow_menu_text_clicked_should_show_alert_dialog() {
        val reminder = ReminderTestUtil().createReminderState()
        setContent(reminder)

        composeTestRule.apply {
            onNodeWithContentDescription("More").performClick()
            onNodeWithText("Delete").performClick()

            onNodeWithText("Delete this reminder?").assertIsDisplayed()
            onNodeWithText("Cancel").assertIsDisplayed()
            onNodeWithText("Delete").assertIsDisplayed()
        }
    }

    @Test
    fun when_delete_dialog_cancel_button_clicked_should_dismiss_dialog() {
        val reminder = ReminderTestUtil().createReminderState()
        setContent(reminder)

        composeTestRule.apply {
            onNodeWithContentDescription("More").performClick()
            onNodeWithText("Delete").performClick()

            onNodeWithText("Cancel").performClick()

            onNodeWithText("Delete this reminder?").assertDoesNotExist()
            onNodeWithText("Cancel").assertDoesNotExist()
            onNodeWithText("Delete").assertDoesNotExist()
        }
    }

    @Test
    fun when_complete_overflow_menu_text_clicked_should_show_alert_dialog() {
        val reminder = ReminderTestUtil().createReminderState()
        setContent(reminder)
        composeTestRule.apply {
            onNodeWithContentDescription("More").performClick()
            onNodeWithText("Complete").performClick()

            onNodeWithText("Complete this reminder?").assertIsDisplayed()
            onNodeWithText("Cancel").assertIsDisplayed()
            onNodeWithText("Complete").assertIsDisplayed()
        }
    }

    @Test
    fun when_complete_dialog_cancel_button_clicked_should_dismiss_dialog() {
        val reminder = ReminderTestUtil().createReminderState()
        setContent(reminder)

        composeTestRule.apply {
            onNodeWithContentDescription("More").performClick()
            onNodeWithText("Complete").performClick()

            onNodeWithText("Cancel").performClick()

            onNodeWithText("Complete this reminder?").assertDoesNotExist()
            onNodeWithText("Cancel").assertDoesNotExist()
            onNodeWithText("Delete").assertDoesNotExist()
        }
    }
}

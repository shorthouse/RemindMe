package dev.shorthouse.remindme.compose

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.shorthouse.remindme.HiltTestActivity
import dev.shorthouse.remindme.compose.screen.ReminderDetailsScaffold
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.model.RepeatInterval
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.util.TestUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@HiltAndroidTest
class ReminderDetailsTest {

    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    private val reminders = listOf(
        TestUtil().createReminder(
            id = 1L,
            name = "Test Reminder Details",
            startDateTime = ZonedDateTime.parse("2000-01-01T14:02:00Z")
        ),
        TestUtil().createReminder(
            id = 2L,
            name = "Test Reminder Details with Repeat Interval",
            startDateTime = ZonedDateTime.parse("2000-01-01T14:02:00Z"),
            repeatInterval = RepeatInterval(2, ChronoUnit.WEEKS)
        ),
        TestUtil().createReminder(
            id = 3L,
            name = "Test Reminder Details with Notes",
            notes = "notes"
        ),
        TestUtil().createReminder(
            id = 4L,
            name = "Test Reminder Details with Notification Enabled",
            isNotificationSent = true
        ),
        TestUtil().createReminder(
            id = 5L,
            name = "Test Reminder Details with Everything",
            startDateTime = ZonedDateTime.parse("2000-01-01T14:02:00Z"),
            repeatInterval = RepeatInterval(2, ChronoUnit.WEEKS),
            isNotificationSent = true,
            notes = "notes"
        ),
        TestUtil().createReminder(
            id = 6L,
            name = "m".repeat(200)
        ),
        TestUtil().createReminder(
            id = 7L,
            name = "Test Reminder Large Notes",
            notes = "m".repeat(1000),
            isNotificationSent = true
        ),
        TestUtil().createReminder(
            id = 8L,
            name = "Test Completed Reminder Details",
            startDateTime = ZonedDateTime.parse("2000-01-01T14:02:00Z"),
            repeatInterval = RepeatInterval(2, ChronoUnit.WEEKS),
            isNotificationSent = true,
            notes = "notes",
            isCompleted = true
        )
    )

    @Before
    fun setup() {
        hiltTestRule.inject()
    }

    private fun setContent(reminder: Reminder) {
        composeTestRule.setContent {
            RemindMeTheme {
                ReminderDetailsScaffold(
                    reminderState = ReminderState(reminder),
                    onNavigateUp = {},
                    onEdit = {},
                    onDelete = {},
                    onComplete = {}
                )
            }
        }
    }

    @Test
    fun when_reminder_details_created_should_display_top_app_bar() {
        setContent(reminders[0])

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
        setContent(reminders[7])

        composeTestRule.apply {
            onNodeWithText("Details").assertIsDisplayed()
            onNodeWithContentDescription("Back").assertIsDisplayed()
            onNodeWithContentDescription("Delete").assertIsDisplayed()

            onNodeWithContentDescription("Edit").assertDoesNotExist()
            onNodeWithContentDescription("More").assertDoesNotExist()
        }
    }

    @Test
    fun when_reminder_details_created_should_display_base_details() {
        setContent(reminders[0])

        composeTestRule.apply {
            onNodeWithText("Test Reminder Details").assertIsDisplayed()

            onNodeWithContentDescription("Date").assertIsDisplayed()
            onNodeWithText("Sat, 01 Jan 2000").assertIsDisplayed()

            onNodeWithContentDescription("Time").assertIsDisplayed()
            onNodeWithText("14:02").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_has_notification_enabled_should_display_notification_section() {
        setContent(reminders[3])

        composeTestRule.apply {
            onNodeWithText("Test Reminder Details with Notification Enabled").assertIsDisplayed()
            onNodeWithContentDescription("Notification").assertIsDisplayed()
            onNodeWithText("Notifications enabled").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_has_repeat_interval_should_display_repeat_interval_section() {
        setContent(reminders[1])

        composeTestRule.apply {
            onNodeWithText("Test Reminder Details with Repeat Interval").assertIsDisplayed()
            onNodeWithContentDescription("Repeat Interval").assertIsDisplayed()
            onNodeWithText("2 Weeks").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_has_notes_should_display_notes_section() {
        setContent(reminders[2])

        composeTestRule.apply {
            onNodeWithText("Test Reminder Details with Notes").assertIsDisplayed()
            onNodeWithContentDescription("Notes").assertIsDisplayed()
            onNodeWithText("notes").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_has_all_optional_parts_enabled_should_display_all_optional_parts() {
        setContent(reminders[4])

        composeTestRule.apply {
            onNodeWithText("Test Reminder Details with Everything").assertIsDisplayed()

            onNodeWithContentDescription("Date").assertIsDisplayed()
            onNodeWithContentDescription("Time").assertIsDisplayed()
            onNodeWithContentDescription("Notification").assertIsDisplayed()
            onNodeWithContentDescription("Repeat Interval").assertIsDisplayed()
            onNodeWithContentDescription("Notes").assertIsDisplayed()

            onNodeWithText("Sat, 01 Jan 2000").assertIsDisplayed()
            onNodeWithText("14:02").assertIsDisplayed()
            onNodeWithText("Notifications enabled").assertIsDisplayed()
            onNodeWithText("2 Weeks").assertIsDisplayed()
            onNodeWithText("notes").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_with_largest_possible_name_should_display_correctly() {
        setContent(reminders[5])

        composeTestRule.apply {
            onNodeWithText("m".repeat(200)).assertIsDisplayed()
        }
    }

    @Test
    fun when_delete_overflow_menu_text_clicked_should_show_alert_dialog() {
        setContent(reminders[0])

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
        setContent(reminders[0])

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
        setContent(reminders[0])

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
        setContent(reminders[0])

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

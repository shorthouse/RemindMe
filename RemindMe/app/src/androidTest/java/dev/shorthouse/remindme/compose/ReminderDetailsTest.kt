package dev.shorthouse.remindme.compose

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.accompanist.themeadapter.material.MdcTheme
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.shorthouse.remindme.HiltTestActivity
import dev.shorthouse.remindme.compose.screen.ReminderDetailsScaffold
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.model.RepeatInterval
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
        )
    )

    @Before
    fun setup() {
        hiltTestRule.inject()
    }

    private fun setDetailsContent(reminder: Reminder) {
        composeTestRule.setContent {
            MdcTheme {
                ReminderDetailsScaffold(
                    reminderState = ReminderState(reminder),
                    onDelete = {},
                    onComplete = {},
                    navigator = EmptyDestinationsNavigator
                )
            }
        }
    }

    @Test
    fun when_reminder_details_created_should_display_top_app_bar() {
        setDetailsContent(reminders[0])

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
    fun when_reminder_details_created_should_display_base_details() {
        setDetailsContent(reminders[0])

        composeTestRule.apply {
            onNodeWithText("Test Reminder Details").assertIsDisplayed()

            onNodeWithContentDescription("Calendar icon").assertIsDisplayed()
            onNodeWithText("Sat, 01 Jan 2000").assertIsDisplayed()

            onNodeWithContentDescription("Clock icon").assertIsDisplayed()
            onNodeWithText("14:02").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_has_repeat_interval_should_display_repeat_interval_section() {
        setDetailsContent(reminders[1])

        composeTestRule.apply {
            onNodeWithText("Test Reminder Details with Repeat Interval").assertIsDisplayed()
            onNodeWithContentDescription("Repeat icon").assertIsDisplayed()
            onNodeWithText("2 weeks").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_has_notes_should_display_notes_section() {
        setDetailsContent(reminders[2])

        composeTestRule.apply {
            onNodeWithText("Test Reminder Details with Notes").assertIsDisplayed()
            onNodeWithContentDescription("Notes icon").assertIsDisplayed()
            onNodeWithText("notes").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_has_notification_enabled_should_display_notification_section() {
        setDetailsContent(reminders[3])

        composeTestRule.apply {
            onNodeWithText("Test Reminder Details with Notification Enabled").assertIsDisplayed()
            onNodeWithContentDescription("Notification icon").assertIsDisplayed()
            onNodeWithText("Notifications enabled").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_has_all_optional_parts_enabled_should_display_all_optional_parts() {
        setDetailsContent(reminders[4])

        composeTestRule.apply {
            onNodeWithText("Test Reminder Details with Everything").assertIsDisplayed()

            onNodeWithContentDescription("Calendar icon").assertIsDisplayed()
            onNodeWithContentDescription("Clock icon").assertIsDisplayed()
            onNodeWithContentDescription("Repeat icon").assertIsDisplayed()
            onNodeWithContentDescription("Notes icon").assertIsDisplayed()
            onNodeWithContentDescription("Notification icon").assertIsDisplayed()

            onNodeWithText("Sat, 01 Jan 2000").assertIsDisplayed()
            onNodeWithText("14:02").assertIsDisplayed()
            onNodeWithText("2 weeks").assertIsDisplayed()
            onNodeWithText("notes").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_with_largest_possible_name_should_display_correctly() {
        setDetailsContent(reminders[5])

        composeTestRule.apply {
            onNodeWithText("m".repeat(200)).assertIsDisplayed()
        }
    }

    @Test
    fun when_delete_icon_clicked_should_show_alert_dialog() {
        setDetailsContent(reminders[0])

        composeTestRule.apply {
            onNodeWithContentDescription("More").performClick()
            onNodeWithText("Delete").performClick()

            onNodeWithText("Delete this reminder?").assertIsDisplayed()
            onNodeWithText("Cancel").assertIsDisplayed()
            onNodeWithText("Delete").assertIsDisplayed()
        }
    }

    @Test
    fun when_delete_dialog_cancel_button_clicked_should_dismiss_delete_dialog() {
        setDetailsContent(reminders[0])

        composeTestRule.apply {
            onNodeWithContentDescription("More").performClick()
            onNodeWithText("Delete").performClick()

            onNodeWithText("Cancel").performClick()

            onNodeWithText("Delete this reminder?").assertDoesNotExist()
            onNodeWithText("Cancel").assertDoesNotExist()
            onNodeWithText("Delete").assertDoesNotExist()
        }
    }
}

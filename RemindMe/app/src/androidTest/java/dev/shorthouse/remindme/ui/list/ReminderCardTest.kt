package dev.shorthouse.remindme.ui.list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.shorthouse.remindme.HiltTestActivity
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.model.RepeatInterval
import dev.shorthouse.remindme.ui.theme.AppTheme
import dev.shorthouse.remindme.util.ReminderTestUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@HiltAndroidTest
class ReminderCardTest {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltTestRule.inject()
    }

    private fun setContent(reminder: Reminder) {
        composeTestRule.setContent {
            AppTheme {
                ReminderCard(
                    reminder = reminder,
                    onReminderCard = {}
                )
            }
        }
    }

    @Test
    fun when_reminder_card_created_should_display_required_fields() {
        val reminder = ReminderTestUtil().createReminder(
            name = "Reminder name",
            startDateTime = ZonedDateTime.parse("3020-01-01T08:30:00Z")
        )

        setContent(reminder)

        composeTestRule.apply {
            onNodeWithText("Reminder name").assertIsDisplayed()
            onNodeWithText("Sat, 01 Jan 3020 • 08:30")
        }
    }

    @Test
    fun when_overdue_reminder_card_created_should_display_overdue_tag() {
        val reminder = ReminderTestUtil().createReminder(
            startDateTime = ZonedDateTime.parse("2020-01-01T08:30:00Z")
        )

        setContent(reminder)

        composeTestRule.apply {
            onNodeWithText("Overdue").assertIsDisplayed()
        }
    }

    @Test
    fun when_upcoming_reminder_card_created_should_display_scheduled_tag() {
        val reminder = ReminderTestUtil().createReminder(
            startDateTime = ZonedDateTime.parse("3020-01-01T08:30:00Z")
        )

        setContent(reminder)

        composeTestRule.apply {
            onNodeWithText("Upcoming").assertIsDisplayed()
        }
    }

    @Test
    fun when_completed_reminder_card_created_should_display_completed_tag() {
        val reminder = ReminderTestUtil().createReminder(
            isCompleted = true
        )

        setContent(reminder)

        composeTestRule.apply {
            onNodeWithText("Completed").assertIsDisplayed()
        }
    }

    @Test
    fun when_notifying_reminder_card_created_should_display_notification_icon() {
        val reminder = ReminderTestUtil().createReminder(
            isNotificationSent = true
        )

        setContent(reminder)

        composeTestRule.apply {
            onNodeWithContentDescription("Notification").assertIsDisplayed()
        }
    }

    @Test
    fun when_repeating_reminder_card_created_should_display_notification_icon() {
        val reminder = ReminderTestUtil().createReminder(
            repeatInterval = RepeatInterval(4, ChronoUnit.DAYS)
        )

        setContent(reminder)

        composeTestRule.apply {
            onNodeWithContentDescription("Repeat Interval").assertIsDisplayed()
            onNodeWithText("4 days").assertIsDisplayed()
        }
    }

    @Test
    fun when_notes_reminder_card_created_should_display_notification_icon() {
        val reminder = ReminderTestUtil().createReminder(
            notes = "notes"
        )

        setContent(reminder)

        composeTestRule.apply {
            onNodeWithContentDescription("Notes").assertIsDisplayed()
            onNodeWithText("notes").assertIsDisplayed()
        }
    }
}

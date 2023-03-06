package dev.shorthouse.remindme.ui.component.list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.shorthouse.remindme.HiltTestActivity
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.theme.RemindMeTheme
import dev.shorthouse.remindme.util.ReminderTestUtil
import java.time.LocalTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ReminderListCardTest {
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
                ReminderListCard(
                    reminderState = reminderState,
                    onReminderCard = {}
                )
            }
        }
    }

    @Test
    fun when_reminder_list_card_created_should_display_required_fields() {
        val reminderState = ReminderTestUtil().createReminderState(
            name = "Reminder name",
            date = "Sat, 01 Jan 3020",
            time = LocalTime.parse("08:30")
        )

        setContent(reminderState)

        composeTestRule.apply {
            onNodeWithText("Reminder name").assertIsDisplayed()
            onNodeWithText("Sat, 01 Jan 3020 • 08:30")
        }
    }

    @Test
    fun when_overdue_reminder_list_card_created_should_display_overdue_tag() {
        val reminderState = ReminderTestUtil().createReminderState(
            date = "Wed, 01 Jan 2020",
            time = LocalTime.parse("08:30")
        )

        setContent(reminderState)

        composeTestRule.apply {
            onNodeWithText("Overdue").assertIsDisplayed()
        }
    }

    @Test
    fun when_scheduled_reminder_list_card_created_should_display_scheduled_tag() {
        val reminderState = ReminderTestUtil().createReminderState(
            date = "Sat, 01 Jan 3020",
            time = LocalTime.parse("08:30")
        )

        setContent(reminderState)

        composeTestRule.apply {
            onNodeWithText("Scheduled").assertIsDisplayed()
        }
    }

    @Test
    fun when_completed_reminder_list_card_created_should_display_completed_tag() {
        val reminderState = ReminderTestUtil().createReminderState(
            isCompleted = true
        )

        setContent(reminderState)

        composeTestRule.apply {
            onNodeWithText("Completed").assertIsDisplayed()
        }
    }

    @Test
    fun when_notifying_reminder_list_card_created_should_display_notification_icon() {
        val reminderState = ReminderTestUtil().createReminderState(
            isNotificationSent = true
        )

        setContent(reminderState)

        composeTestRule.apply {
            onNodeWithContentDescription("Notification").assertIsDisplayed()
        }
    }

    @Test
    fun when_repeating_reminder_list_card_created_should_display_notification_icon() {
        val reminderState = ReminderTestUtil().createReminderState(
            isRepeatReminder = true,
            repeatAmount = "4",
            repeatUnit = "days"
        )

        setContent(reminderState)

        composeTestRule.apply {
            onNodeWithContentDescription("Repeat Interval").assertIsDisplayed()
            onNodeWithText("4 days").assertIsDisplayed()
        }
    }

    @Test
    fun when_notes_reminder_list_card_created_should_display_notification_icon() {
        val reminderState = ReminderTestUtil().createReminderState(
            notes = "notes"
        )

        setContent(reminderState)

        composeTestRule.apply {
            onNodeWithContentDescription("Notes").assertIsDisplayed()
            onNodeWithText("notes").assertIsDisplayed()
        }
    }
}

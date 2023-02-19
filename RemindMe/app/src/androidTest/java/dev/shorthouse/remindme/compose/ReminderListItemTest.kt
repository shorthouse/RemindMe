package dev.shorthouse.remindme.compose

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.shorthouse.remindme.HiltTestActivity
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.theme.RemindMeTheme
import dev.shorthouse.remindme.util.ReminderTestUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ReminderListItemTest {
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
                //ReminderListItem(reminderState = reminderState)
            }
        }
    }

    @Test
    fun when_reminder_has_notification_should_show_notification_icon() {
        val notificationReminder = ReminderTestUtil().createReminderListItemState(
            isNotificationSent = true,
            isRepeatReminder = false
        )
        setContent(notificationReminder)

        composeTestRule.apply {
            onNodeWithContentDescription("Notifications enabled").assertIsDisplayed()
            onNodeWithContentDescription("Repeat reminder").assertDoesNotExist()
        }
    }

    @Test
    fun when_reminder_is_repeating_should_show_repeat_icon() {
        val repeatReminder = ReminderTestUtil().createReminderListItemState(
            isNotificationSent = false,
            isRepeatReminder = true
        )
        setContent(repeatReminder)

        composeTestRule.apply {
            onNodeWithContentDescription("Repeat reminder").assertIsDisplayed()
            onNodeWithContentDescription("Notifications enabled").assertDoesNotExist()
        }
    }

    @Test
    fun when_reminder_is_repeating_and_has_notification_should_show_both_icons() {
        val notificationRepeatReminder = ReminderTestUtil().createReminderListItemState(
            isNotificationSent = true,
            isRepeatReminder = true
        )
        setContent(notificationRepeatReminder)
        composeTestRule.apply {
            onNodeWithContentDescription("Repeat reminder").assertIsDisplayed()
            onNodeWithContentDescription("Notifications enabled").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_is_not_repeating_and_does_not_have_notification_should_show_no_icons() {
        val noNotificationNotRepeatReminder = ReminderTestUtil().createReminderListItemState(
            isNotificationSent = false,
            isRepeatReminder = false
        )
        setContent(noNotificationNotRepeatReminder)
        composeTestRule.apply {
            onNodeWithContentDescription("Repeat reminder").assertDoesNotExist()
            onNodeWithContentDescription("Notifications enabled").assertDoesNotExist()
        }
    }
}

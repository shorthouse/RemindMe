package dev.shorthouse.remindme.compose

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.shorthouse.remindme.HiltTestActivity
import dev.shorthouse.remindme.compose.screen.ReminderListCompletedContent
import dev.shorthouse.remindme.compose.state.ReminderState
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.util.TestUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ReminderListCompletedTest {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltTestRule.inject()
    }

    private fun setContent(reminderStates: List<ReminderState>) {
        composeTestRule.setContent {
            RemindMeTheme {
                ReminderListCompletedContent(
                    reminderStates = reminderStates,
                    onNavigateDetails = {}
                )
            }
        }
    }

    @Test
    fun when_scheduled_reminder_list_created_should_display_expected_content() {
        setContent(listOf(TestUtil.defaultReminderState))

        composeTestRule.apply {
            onNodeWithText("Yoga with Alice").assertIsDisplayed()
            onNodeWithText("Wed, 22 Mar 2000").assertIsDisplayed()
            onNodeWithText("14:30").assertIsDisplayed()

            onNodeWithContentDescription("Complete reminder").assertDoesNotExist()
            onNodeWithContentDescription("Notifications enabled").assertDoesNotExist()
            onNodeWithContentDescription("Repeat reminder").assertDoesNotExist()
        }
    }

    @Test
    fun when_scheduled_reminder_state_changes_should_show_expected_icons() {
        val notificationReminder = TestUtil().createReminderListItemState(isNotificationSent = true)
        setContent(listOf(notificationReminder))
        composeTestRule.apply {
            onNodeWithContentDescription("Notifications enabled").assertIsDisplayed()
            onNodeWithContentDescription("Repeat reminder").assertDoesNotExist()
        }

        val repeatReminder = TestUtil().createReminderListItemState(isRepeatReminder = true)
        setContent(listOf(repeatReminder))
        composeTestRule.apply {
            onNodeWithContentDescription("Repeat reminder").assertIsDisplayed()
            onNodeWithContentDescription("Notifications enabled").assertDoesNotExist()
        }

        val notificationRepeatReminder = TestUtil().createReminderListItemState(
            isNotificationSent = true,
            isRepeatReminder = true
        )
        setContent(listOf(notificationRepeatReminder))
        composeTestRule.apply {
            onNodeWithContentDescription("Repeat reminder").assertIsDisplayed()
            onNodeWithContentDescription("Notifications enabled").assertIsDisplayed()
        }
    }
}

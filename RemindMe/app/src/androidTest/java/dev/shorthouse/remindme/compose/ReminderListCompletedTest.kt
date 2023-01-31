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
import dev.shorthouse.remindme.util.ReminderTestUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalTime

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
        val reminderState = ReminderTestUtil().createReminderState(
            name = "Yoga with Alice",
            date = "Wed, 22 Mar 2000",
            time = LocalTime.of(14, 30),
            isNotificationSent = false,
            isRepeatReminder = false
        )
        setContent(listOf(reminderState))

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
    fun when_completed_reminder_list_empty_should_show_empty_state() {
        setContent(emptyList())

        composeTestRule.apply {
            onNodeWithText("No completed reminders").assertIsDisplayed()
            onNodeWithText("Completed reminders will show up here").assertIsDisplayed()
        }
    }
}

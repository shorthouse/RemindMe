package dev.shorthouse.remindme.ui.component.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.shorthouse.remindme.HiltTestActivity
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.theme.m2.RemindMeTheme
import dev.shorthouse.remindme.util.ReminderTestUtil
import java.time.LocalTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ReminderListTest {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltTestRule.inject()
    }

    private fun setContent(
        reminderStates: List<ReminderState>,
        emptyStateContent: @Composable () -> Unit,
        onReminderCard: (ReminderState) -> Unit
    ) {
        composeTestRule.setContent {
            RemindMeTheme {
                ReminderList(
                    reminderStates = reminderStates,
                    emptyStateContent = emptyStateContent,
                    onReminderCard = onReminderCard,
                    contentPadding = PaddingValues(0.dp),
                    reminderFilters = reminderFilters
                )
            }
        }
    }

    @Test
    fun when_reminder_list_created_with_reminders_should_display_reminders() {
        setContent(
            reminderStates = listOf(
                ReminderTestUtil().createReminderState(
                    name = "Test Reminder List 1",
                    date = "Sat, 01 Jan 2000",
                    time = LocalTime.parse("08:00"),
                    isNotificationSent = true
                ),
                ReminderTestUtil().createReminderState(
                    name = "Test Reminder List 2",
                    date = "Wed, 01 Jan 3000",
                    time = LocalTime.parse("09:00"),
                    isRepeatReminder = true,
                    repeatAmount = "1",
                    repeatUnit = "day"
                ),
                ReminderTestUtil().createReminderState(
                    name = "Test Reminder List 3",
                    date = "Wed, 01 Jan 3000",
                    time = LocalTime.parse("10:00"),
                    notes = "notes",
                    isCompleted = true
                )
            ),
            emptyStateContent = {},
            onReminderCard = {}
        )

        composeTestRule.apply {
            onNodeWithText("Test Reminder List 1").assertIsDisplayed()
            onNodeWithText("Sat, 01 Jan 2000 • 08:00").assertIsDisplayed()
            onNodeWithText("Overdue").assertIsDisplayed()

            onNodeWithText("Test Reminder List 2").assertIsDisplayed()
            onNodeWithText("Wed, 01 Jan 3000 • 09:00").assertIsDisplayed()
            onNodeWithText("Scheduled").assertIsDisplayed()

            onNodeWithText("Test Reminder List 3").assertIsDisplayed()
            onNodeWithText("Wed, 01 Jan 3000 • 10:00").assertIsDisplayed()
            onNodeWithText("Completed").assertIsDisplayed()

            onNodeWithContentDescription("Notification").assertIsDisplayed()
            onNodeWithContentDescription("Repeat Interval").assertIsDisplayed()
            onNodeWithText("1 day").assertIsDisplayed()
            onNodeWithContentDescription("Notes").assertIsDisplayed()
            onNodeWithText("notes").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_list_created_with_empty_reminders_should_display_empty_state() {
        setContent(
            reminderStates = emptyList(),
            emptyStateContent = { Text("Empty state") },
            onReminderCard = {}
        )

        composeTestRule.apply {
            onNodeWithText("Empty state").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_clicked_should_call_on_reminder_card() {
        var isOnReminderCardCalled = false

        setContent(
            reminderStates = listOf(
                ReminderTestUtil().createReminderState(
                    name = "Test Click Reminder"
                )
            ),
            emptyStateContent = { },
            onReminderCard = { isOnReminderCardCalled = true }
        )

        composeTestRule.apply {
            onNodeWithText("Test Click Reminder").performClick()
            assertThat(isOnReminderCardCalled).isTrue()
        }
    }
}

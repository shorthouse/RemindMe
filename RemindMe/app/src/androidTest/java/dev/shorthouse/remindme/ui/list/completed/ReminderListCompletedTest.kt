package dev.shorthouse.remindme.ui.list.completed

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.shorthouse.remindme.HiltTestActivity
import dev.shorthouse.remindme.ui.screen.list.completed.ReminderListCompletedScaffold
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.theme.m2.RemindMeTheme
import dev.shorthouse.remindme.util.ReminderTestUtil
import java.time.LocalTime
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

    private fun setContent(
        completedReminderStates: List<ReminderState> = listOf(
            ReminderTestUtil().createReminderState(
                name = "Test Completed Reminder",
                date = "Wed, 01 Jan 2020",
                time = LocalTime.parse("08:00"),
                isCompleted = true
            )
        ),
        isLoading: Boolean = false
    ) {
        composeTestRule.setContent {
            RemindMeTheme {
                ReminderListCompletedScaffold(
                    completedReminderStates = completedReminderStates,
                    isLoading = isLoading,
                    onNavigateUp = {},
                    onDeleteCompletedReminders = {},
                    onReminderCard = {}
                )
            }
        }
    }

    @Test
    fun when_reminder_list_completed_created_should_display_expected_scaffold_content() {
        setContent(
            completedReminderStates = emptyList()
        )

        composeTestRule.apply {
            onNodeWithContentDescription("Back").assertIsDisplayed()
            onNodeWithText("Completed").assertIsDisplayed()
            onNodeWithContentDescription("Delete completed reminders").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_list_completed_created_should_display_expected_list_content() {
        setContent()

        composeTestRule.apply {
            onNodeWithText("Test Completed Reminder").assertIsDisplayed()
            onNodeWithText("Wed, 01 Jan 2020 • 08:00").assertIsDisplayed()
        }
    }

    @Test
    fun when_delete_completed_reminders_icon_click_should_display_delete_dialog() {
        setContent()

        composeTestRule.apply {
            onNodeWithContentDescription("Delete completed reminders").performClick()

            onNodeWithText("Delete completed reminders?").assertIsDisplayed()
            onNodeWithText("Cancel").assertIsDisplayed()
            onNodeWithText("Delete").assertIsDisplayed()
        }
    }

    @Test
    fun when_loading_reminder_list_completed_should_display_scaffold_with_blank_list() {
        setContent(
            isLoading = true
        )

        composeTestRule.apply {
            onNodeWithContentDescription("Back").assertIsDisplayed()
            onNodeWithText("Completed").assertIsDisplayed()
            onNodeWithContentDescription("Delete completed reminders").assertIsDisplayed()

            onNodeWithText("Test Completed Reminder").assertDoesNotExist()
            onNodeWithText("Wed, 01 Jan 2020 • 08:00").assertDoesNotExist()
        }
    }

    @Test
    fun when_reminder_list_completed_is_empty_should_display_empty_state() {
        setContent(
            completedReminderStates = emptyList()
        )

        composeTestRule.apply {
            onNodeWithText("No completed reminders").assertIsDisplayed()
            onNodeWithText("Completed reminders will show up here").assertIsDisplayed()
        }
    }
}

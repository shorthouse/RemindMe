package dev.shorthouse.remindme.ui.component.sheet

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
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
class BottomSheetTest {
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
                BottomSheetReminderActions(
                    reminderState = reminderState,
                    onReminderActionItemSelected = {}
                )
            }
        }
    }

    @Test
    fun when_completed_reminder_bottom_sheet_opens_should_display_expected_actions() {
        setContent(
            reminderState = ReminderTestUtil().createReminderState(
                isCompleted = true
            )
        )

        composeTestRule.apply {
            onNodeWithText("Delete").assertIsDisplayed()
        }
    }

    @Test
    fun when_onetime_uncompleted_reminder_bottom_sheet_opens_should_display_expected_actions() {
        setContent(
            reminderState = ReminderTestUtil().createReminderState(
                isRepeatReminder = false
            )
        )

        composeTestRule.apply {
            onNodeWithText("Complete").assertIsDisplayed()
            onNodeWithText("Edit").assertIsDisplayed()
            onNodeWithText("Delete").assertIsDisplayed()
        }
    }

    @Test
    fun when_repeat_uncompleted_reminder_bottom_sheet_opens_should_display_expected_actions() {
        setContent(
            reminderState = ReminderTestUtil().createReminderState(
                isRepeatReminder = true
            )
        )

        composeTestRule.apply {
            onNodeWithText("Complete").assertIsDisplayed()
            onNodeWithText("Complete Series").assertIsDisplayed()
            onNodeWithText("Edit").assertIsDisplayed()
            onNodeWithText("Delete").assertIsDisplayed()
        }
    }
}

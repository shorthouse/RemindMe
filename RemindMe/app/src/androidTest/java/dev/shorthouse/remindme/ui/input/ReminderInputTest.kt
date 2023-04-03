package dev.shorthouse.remindme.ui.input

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.shorthouse.remindme.HiltTestActivity
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.theme.AppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalTime

@HiltAndroidTest
class ReminderInputTest {

    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltTestRule.inject()
    }

    private val reminderAddState = ReminderState(
        id = 1,
        name = "",
        date = "Sat, 01 Jan 2000",
        time = LocalTime.of(14, 30),
        isNotificationSent = false,
        isRepeatReminder = false,
        repeatAmount = "1",
        repeatUnit = "Day",
        notes = "",
        isCompleted = false
    )

    private val reminderEditState = ReminderState(
        id = 1,
        name = "Reminder name",
        date = "Sun, 02 Jan 2000",
        time = LocalTime.of(18, 30),
        isNotificationSent = true,
        isRepeatReminder = true,
        repeatAmount = "2",
        repeatUnit = "Weeks",
        notes = "Reminder notes",
        isCompleted = false
    )

    private fun setAddContent() {
        composeTestRule.setContent {
            AppTheme {
                ReminderInputScaffold(
                    reminderState = reminderAddState,
                    snackbarHostState = SnackbarHostState(),
                    topBarTitle = "Add Reminder",
                    onNavigateUp = {},
                    onSave = {}
                )
            }
        }
    }

    private fun setContent() {
        composeTestRule.setContent {
            AppTheme {
                ReminderInputScaffold(
                    reminderState = reminderEditState,
                    snackbarHostState = SnackbarHostState(),
                    topBarTitle = "Edit Reminder",
                    onNavigateUp = {},
                    onSave = {}
                )
            }
        }
    }

    @Test
    fun when_reminder_add_created_should_display_content() {
        setAddContent()

        composeTestRule.apply {
            onNodeWithText("Add Reminder").assertIsDisplayed()
            onNodeWithContentDescription("Close").assertIsDisplayed()
            onNodeWithContentDescription("Save reminder").assertIsDisplayed()

            onNodeWithText("RemindMe to…", useUnmergedTree = true).assertIsDisplayed()

            onNodeWithContentDescription("Date").assertIsDisplayed()
            onNodeWithText("Sat, 01 Jan 2000").assertIsDisplayed()

            onNodeWithContentDescription("Time").assertIsDisplayed()
            onNodeWithText("14:30").assertIsDisplayed()

            onNodeWithContentDescription("Notification").assertIsDisplayed()
            onNodeWithText("Send notification").assertIsDisplayed()
            onNodeWithTag("Test Tag Switch Notification").assertIsOff()

            onNodeWithText("Repeat reminder").assertIsDisplayed()
            onNodeWithTag("Test Tag Switch Repeat Interval").assertIsOff()

            onNodeWithContentDescription("Notes").assertIsDisplayed()
            onNodeWithText("Add notes").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_edit_created_should_display_content() {
        setContent()

        composeTestRule.apply {
            onNodeWithText("Edit Reminder").assertIsDisplayed()
            onNodeWithContentDescription("Close").assertIsDisplayed()
            onNodeWithContentDescription("Save reminder").assertIsDisplayed()
            onNodeWithText("Reminder name").assertIsDisplayed()

            onNodeWithContentDescription("Date").assertIsDisplayed()
            onNodeWithText("Sun, 02 Jan 2000").assertIsDisplayed()

            onNodeWithContentDescription("Time").assertIsDisplayed()
            onNodeWithText("18:30").assertIsDisplayed()

            onNodeWithContentDescription("Notification").assertIsDisplayed()
            onNodeWithText("Send notification").assertIsDisplayed()
            onNodeWithTag("Test Tag Switch Repeat Interval").assertIsOn()

            onNodeWithText("Repeat reminder").assertIsDisplayed()
            onNodeWithTag("Test Tag Switch Repeat Interval").assertIsOn()

            onNodeWithText("Repeats every").assertIsDisplayed()
            onNodeWithText("2").assertIsDisplayed()
            onNodeWithText("Days").assertIsDisplayed()
            onNodeWithText("Weeks").assertIsDisplayed()
            onNodeWithTag("Days").assertIsNotSelected()
            onNodeWithTag("Weeks").assertIsSelected()

            onNodeWithContentDescription("Notes").assertIsDisplayed()
            onNodeWithText("Reminder notes").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_date_clicked_should_display_date_picker() {
        setContent()

        composeTestRule.apply {
            onNodeWithContentDescription("Date").performClick()
            waitForIdle()
            onNodeWithText("Jan 2, 2000").assertIsDisplayed()
            onNodeWithText("January 2000").assertIsDisplayed()
            onNodeWithText("Cancel").assertIsDisplayed()
            onNodeWithText("OK").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_time_clicked_should_display_time_picker() {
        setContent()

        composeTestRule.apply {
            onNodeWithContentDescription("Time").performClick()
            waitForIdle()
            onNodeWithContentDescription("Select hour").assertIsDisplayed()
            onNodeWithText("Cancel").assertIsDisplayed()
            onNodeWithText("OK").assertIsDisplayed()
        }
    }

    @Test
    fun when_repeat_reminder_switch_toggled_should_toggle_repeat_input_fields() {
        setContent()

        composeTestRule.apply {
            onNodeWithTag("Test Tag Switch Repeat Interval").assertIsOn()
            onNodeWithText("Repeats every").assertIsDisplayed()
            onNodeWithText("2").assertIsDisplayed()
            onNodeWithText("Days").assertIsDisplayed()
            onNodeWithText("Weeks").assertIsDisplayed()
            onNodeWithTag("Days").assertIsNotSelected()
            onNodeWithTag("Weeks").assertIsSelected()

            onNodeWithTag("Test Tag Switch Repeat Interval").performClick()
            onNodeWithText("Repeats every").assertDoesNotExist()
            onNodeWithText("2").assertDoesNotExist()
            onNodeWithText("Days").assertDoesNotExist()
            onNodeWithText("Weeks").assertDoesNotExist()
            onNodeWithTag("Days").assertDoesNotExist()
            onNodeWithTag("Weeks").assertDoesNotExist()
        }
    }

    @Test
    fun when_repeat_amount_plural_changed_should_change_repeat_unit_plural() {
        setContent()

        composeTestRule.apply {
            onNodeWithTag("Test Tag Text Field Repeat Amount").performTextClearance()
            onNodeWithTag("Test Tag Text Field Repeat Amount").performTextInput("1")
            onNodeWithText("Day").assertIsDisplayed()
            onNodeWithText("Week").assertIsDisplayed()
            onNodeWithText("Days").assertDoesNotExist()
            onNodeWithText("Weeks").assertDoesNotExist()

            onNodeWithTag("Test Tag Text Field Repeat Amount").performTextClearance()
            onNodeWithTag("Test Tag Text Field Repeat Amount").performTextInput("2")
            onNodeWithText("Days").assertIsDisplayed()
            onNodeWithText("Weeks").assertIsDisplayed()
            onNodeWithText("Day").assertDoesNotExist()
            onNodeWithText("Week").assertDoesNotExist()

            onNodeWithTag("Test Tag Text Field Repeat Amount").performTextClearance()
            onNodeWithText("Days").assertIsDisplayed()
            onNodeWithText("Weeks").assertIsDisplayed()
            onNodeWithText("Day").assertDoesNotExist()
            onNodeWithText("Week").assertDoesNotExist()
        }
    }
}

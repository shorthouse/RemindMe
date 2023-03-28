package dev.shorthouse.remindme.ui.component.sheet

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.shorthouse.remindme.HiltTestActivity
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.ui.theme.AppTheme
import dev.shorthouse.remindme.ui.util.enums.ReminderAction
import dev.shorthouse.remindme.util.ReminderTestUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class BottomSheetReminderActionsTest {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltTestRule.inject()
    }

    private fun setContent(
        reminder: Reminder = Reminder(),
        onReminderActionSelected: (ReminderAction) -> Unit = {},
        onDismissRequest: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            AppTheme {
                BottomSheetReminderActions(
                    reminder = reminder,
                    onReminderActionSelected = onReminderActionSelected,
                    onDismissRequest = onDismissRequest
                )
            }
        }
    }

    @Test
    fun when_reminder_bottom_sheet_opens_should_display_expected_content() {
        setContent(
            reminder = ReminderTestUtil().createReminder(
                name = "Reminder name",
                repeatInterval = null
            )
        )

        composeTestRule.apply {
            onNodeWithText("Reminder name").assertIsDisplayed()
            onNodeWithText("Complete").assertIsDisplayed()
            onNodeWithText("Edit").assertIsDisplayed()
            onNodeWithText("Delete").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_action_clicked_should_call_on_reminder_action_selected() {
        var isOnReminderActionSelectedClicked = false

        setContent(
            onReminderActionSelected = { isOnReminderActionSelectedClicked = true }
        )

        composeTestRule.apply {
            onNodeWithText("Complete").performClick()
            assertThat(isOnReminderActionSelectedClicked).isTrue()
        }
    }

    @Test
    fun when_back_pressed_should_call_on_dismiss_request() {
        var isOnDismissRequested = false

        setContent(
            onDismissRequest = { isOnDismissRequested = true }
        )

        composeTestRule.apply {
            activityRule.scenario.onActivity { activity ->
                activity.onBackPressedDispatcher.onBackPressed()
            }

            assertThat(isOnDismissRequested).isTrue()
        }
    }
}

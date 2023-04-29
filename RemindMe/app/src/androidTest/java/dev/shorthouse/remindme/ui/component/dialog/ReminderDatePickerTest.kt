package dev.shorthouse.remindme.ui.component.dialog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.shorthouse.remindme.HiltTestActivity
import dev.shorthouse.remindme.ui.theme.AppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ReminderDatePickerTest {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltTestRule.inject()
    }

    private fun setContent(
        initialDate: String = "Sat, 01 Jan 2000",
        onConfirm: (String) -> Unit = {},
        onDismiss: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            AppTheme {
                ReminderDatePicker(
                    initialDateOld = initialDate,
                    onConfirm = onConfirm,
                    onDismiss = onDismiss
                )
            }
        }
    }

    @Test
    fun when_reminder_date_picker_displays_should_show_expected_content() {
        setContent()

        composeTestRule.apply {
            onNodeWithText("Jan 1, 2000").assertIsDisplayed()
            onNodeWithText("January 2000").assertIsDisplayed()
            onNodeWithText("Cancel").assertIsDisplayed()
            onNodeWithText("OK").assertIsDisplayed()
        }
    }

    @Test
    fun when_cancel_button_clicked_should_call_on_dismiss() {
        var isOnDismissClicked = false

        setContent(
            onDismiss = { isOnDismissClicked = true }
        )

        composeTestRule.apply {
            onNodeWithText("Cancel").performClick()
            assertThat(isOnDismissClicked).isTrue()
        }
    }

    @Test
    fun when_ok_button_clicked_should_call_on_confirm() {
        var isOnConfirmClicked = false

        setContent(
            onConfirm = { isOnConfirmClicked = true }
        )

        composeTestRule.apply {
            onNodeWithText("OK").performClick()
            assertThat(isOnConfirmClicked).isTrue()
        }
    }
}

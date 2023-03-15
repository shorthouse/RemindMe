package dev.shorthouse.remindme.ui.component.dialog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.shorthouse.remindme.HiltTestActivity
import dev.shorthouse.remindme.ui.theme.m2.RemindMeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class RemindMeAlertDialogTest {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltTestRule.inject()
    }

    private fun setContent(
        onConfirm: () -> Unit = {},
        onDismiss: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            RemindMeTheme {
                RemindMeAlertDialog(
                    title = "Alert dialog title",
                    confirmText = "Confirm",
                    onConfirm = onConfirm,
                    onDismiss = onDismiss
                )
            }
        }
    }

    @Test
    fun when_remindme_alert__dialog_displays_should_show_expected_content() {
        setContent()

        composeTestRule.apply {
            onNodeWithText("Alert dialog title").assertIsDisplayed()
            onNodeWithText("Cancel").assertIsDisplayed()
            onNodeWithText("Confirm").assertIsDisplayed()
        }
    }

    @Test
    fun when_remindme_alert_dialog_buttons_clicked_should_call_expected_functions() {
        var isOnConfirmClicked = false
        var isOnDismissClicked = false

        setContent(
            onConfirm = { isOnConfirmClicked = true },
            onDismiss = { isOnDismissClicked = true }
        )

        composeTestRule.apply {
            onNodeWithText("Confirm").performClick()
            onNodeWithText("Cancel").performClick()

            assertThat(isOnConfirmClicked).isTrue()
            assertThat(isOnDismissClicked).isTrue()
        }
    }
}

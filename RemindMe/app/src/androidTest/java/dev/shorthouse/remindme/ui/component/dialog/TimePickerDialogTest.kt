package dev.shorthouse.remindme.ui.component.dialog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.vanpra.composematerialdialogs.MaterialDialogState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.shorthouse.remindme.HiltTestActivity
import dev.shorthouse.remindme.ui.theme.RemindMeTheme
import dev.shorthouse.remindme.util.ReminderTestUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalTime

@HiltAndroidTest
class TimePickerDialogTest {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltTestRule.inject()
    }

    private fun setContent() {
        composeTestRule.setContent {
            RemindMeTheme {
                TimePickerDialog(
                    time = LocalTime.parse("08:00"),
                    onTimeChange = {},
                    dialogState = MaterialDialogState(
                        initialValue = true
                    )
                )
            }
        }
    }

    @Test
    fun when_time_picker_dialog_displays_should_show_expected_content() {
        setContent()

        composeTestRule.apply {
            onNodeWithText("08").assertIsDisplayed()
            onNodeWithText("00").assertIsDisplayed()
            onNodeWithText("CANCEL").assertIsDisplayed()
            onNodeWithText("OK").assertIsDisplayed()
        }
    }
}

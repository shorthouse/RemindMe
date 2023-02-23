package dev.shorthouse.remindme.ui.component.dialog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.vanpra.composematerialdialogs.MaterialDialogState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.shorthouse.remindme.HiltTestActivity
import dev.shorthouse.remindme.ui.theme.RemindMeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@HiltAndroidTest
class DatePickerDialogTest {
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
                DatePickerDialog(
                    date = LocalDate.of(2020, 1, 1),
                    onDateChange = {},
                    dialogState = MaterialDialogState(initialValue = true)
                )
            }
        }
    }

    @Test
    fun when_date_picker_dialog_displays_should_show_expected_content() {
        setContent()

        composeTestRule.apply {
            onNodeWithText("Wed, Jan 1").assertIsDisplayed()
            onNodeWithText("January 2020").assertIsDisplayed()
            onNodeWithText("CANCEL").assertIsDisplayed()
            onNodeWithText("OK").assertIsDisplayed()
        }
    }
}

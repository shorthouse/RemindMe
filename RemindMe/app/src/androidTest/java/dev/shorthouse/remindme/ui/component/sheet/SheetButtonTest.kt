package dev.shorthouse.remindme.ui.component.sheet

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.SelectAll
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.shorthouse.remindme.HiltTestActivity
import dev.shorthouse.remindme.ui.theme.AppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class SheetButtonTest {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltTestRule.inject()
    }

    private fun setContent(
        buttonIcon: ImageVector,
        buttonLabel: String,
        onSelected: () -> Unit
    ) {
        composeTestRule.setContent {
            AppTheme {
                SheetButton(
                    buttonIcon = buttonIcon,
                    buttonLabel = buttonLabel,
                    onSelected = onSelected
                )
            }
        }
    }

    @Test
    fun when_sheet_button_displays_should_show_expected_content() {
        setContent(
            buttonIcon = Icons.Rounded.Edit,
            buttonLabel = "Edit",
            onSelected = {}
        )

        composeTestRule.apply {
            onNodeWithText("Edit").assertIsDisplayed()
        }
    }

    @Test
    fun when_sheet_button_clicked_should_call_on_selected() {
        var isOnSelectedClicked = false

        setContent(
            buttonIcon = Icons.Rounded.SelectAll,
            buttonLabel = "Select",
            onSelected = { isOnSelectedClicked = true }
        )

        composeTestRule.apply {
            onNodeWithText("Select").performClick()
            Truth.assertThat(isOnSelectedClicked).isTrue()
        }
    }
}

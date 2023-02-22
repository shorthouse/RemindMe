package dev.shorthouse.remindme.ui.component.emptystate

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.shorthouse.remindme.HiltTestActivity
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.ui.theme.RemindMeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class EmptyStateTest {
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
                EmptyState(
                    painter = painterResource(R.drawable.empty_state_active),
                    title = "Test title",
                    subtitle = "Test subtitle"
                )
            }
        }
    }

    @Test
    fun when_empty_state_displays_should_show_expected_title_and_subtitle() {
        setContent()

        composeTestRule.apply {
            onNodeWithText("Test title").assertIsDisplayed()
            onNodeWithText("Test subtitle").assertIsDisplayed()
        }
    }
}

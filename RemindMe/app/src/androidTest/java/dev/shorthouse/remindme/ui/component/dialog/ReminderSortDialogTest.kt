package dev.shorthouse.remindme.ui.component.dialog

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.shorthouse.remindme.HiltTestActivity
import dev.shorthouse.remindme.data.protodatastore.ReminderSortOrder
import dev.shorthouse.remindme.ui.theme.RemindMeTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ReminderSortDialogTest {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltTestRule.inject()
    }

    private fun setContent(
        initialSort: ReminderSortOrder = ReminderSortOrder.BY_EARLIEST_DATE_FIRST,
        onApplySort: (ReminderSortOrder) -> Unit = {}
    ) {
        composeTestRule.setContent {
            RemindMeTheme {
                ReminderSortDialog(
                    initialSort = initialSort,
                    onApplySort = onApplySort,
                    onDismiss = {}
                )
            }
        }
    }

    @Test
    fun when_reminder_sort__dialog_displays_should_show_expected_content() {
        setContent()

        composeTestRule.apply {
            onNodeWithText("Sort reminders by").assertIsDisplayed()
            onNodeWithText("Date (Earliest first)").assertIsDisplayed()
            onNodeWithText("Date (Latest first)").assertIsDisplayed()
            onNodeWithText("Apply").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_sort_dialog_with_earliest_first_sort_should_have_only_earliest_first_selected() {
        setContent(
            initialSort = ReminderSortOrder.BY_EARLIEST_DATE_FIRST
        )

        composeTestRule.apply {
            onNodeWithText("Date (Earliest first)").assertIsSelected()
            onNodeWithText("Date (Latest first)").assertIsNotSelected()
        }
    }

    @Test
    fun when_reminder_sort_dialog_with_latest_first_sort_should_have_only_latest_first_selected() {
        setContent(
            initialSort = ReminderSortOrder.BY_LATEST_DATE_FIRST
        )

        composeTestRule.apply {
            onNodeWithText("Date (Latest first)").assertIsSelected()
            onNodeWithText("Date (Earliest first)").assertIsNotSelected()
        }
    }

    @Test
    fun when_apply_button_clicked_should_call_on_apply_sort() {
        var isOnApplySortClicked = false

        setContent(
            onApplySort = { isOnApplySortClicked = true }
        )

        composeTestRule.apply {
            onNodeWithText("Apply").performClick()
            assertThat(isOnApplySortClicked).isTrue()
        }
    }
}

package dev.shorthouse.remindme.ui.list.active

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.shorthouse.remindme.HiltTestActivity
import dev.shorthouse.remindme.data.protodatastore.ReminderSort
import dev.shorthouse.remindme.ui.screen.list.ReminderListActiveScaffold
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.theme.m2.RemindMeTheme
import dev.shorthouse.remindme.util.ReminderTestUtil
import java.time.LocalTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ReminderListActiveTest {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltTestRule.inject()
    }

    private fun setContent(
        activeReminderStates: List<ReminderState> = listOf(
            ReminderTestUtil().createReminderState(
                name = "Test Overdue Reminder",
                date = "Sat, 01 Jan 2000",
                time = LocalTime.parse("08:00")
            ),
            ReminderTestUtil().createReminderState(
                name = "Test Scheduled Reminder",
                date = "Wed, 01 Jan 3000",
                time = LocalTime.parse("09:00")
            )
        ),
        reminderSortOrder: ReminderSort = ReminderSort.BY_EARLIEST_DATE_FIRST,
        isLoading: Boolean = false
    ) {
        composeTestRule.setContent {
            RemindMeTheme {
                ReminderListActiveScaffold(
                    activeReminderStates = activeReminderStates,
                    reminderSortOrder = reminderSortOrder,
                    reminderFilters = uiState.reminderFilters,
                    onNavigateCompletedReminders = {},
                    onNavigateAdd = {},
                    onReminderCard = {},
                    onApplySort = {},
                    onNavigateSearch = {},
                    isLoading = isLoading,
                    onApplyFilter = onApplyFilter
                )
            }
        }
    }

    @Test
    fun when_reminder_list_active_created_should_display_expected_scaffold_content() {
        setContent(
            activeReminderStates = emptyList()
        )

        composeTestRule.apply {
            onNodeWithText("RemindMe").assertIsDisplayed()
            onNodeWithContentDescription("Sort reminders").assertIsDisplayed()
            onNodeWithContentDescription("Completed reminders").assertIsDisplayed()
            onNodeWithContentDescription("Search reminders").assertIsDisplayed()
            onNodeWithContentDescription("Add reminder").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_list_active_created_should_display_expected_list_content() {
        setContent()

        composeTestRule.apply {
            onNodeWithText("Test Overdue Reminder").assertIsDisplayed()
            onNodeWithText("Sat, 01 Jan 2000 • 08:00").assertIsDisplayed()
            onNodeWithText("Overdue").assertIsDisplayed()

            onNodeWithText("Test Scheduled Reminder").assertIsDisplayed()
            onNodeWithText("Wed, 01 Jan 3000 • 09:00").assertIsDisplayed()
            onNodeWithText("Scheduled").assertIsDisplayed()
        }
    }

    @Test
    fun when_sort_reminders_icon_click_should_display_sort_dialog() {
        setContent()

        composeTestRule.apply {
            onNodeWithContentDescription("Sort reminders").performClick()

            onNodeWithText("Sort reminders by").assertIsDisplayed()
            onNodeWithText("Date (Earliest first)").assertIsDisplayed()
            onNodeWithText("Date (Latest first)").assertIsDisplayed()
            onNodeWithText("Apply").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_list_active_sorted_by_date_earliest_first_should_display_in_expected_order() {
        setContent(
            reminderSortOrder = ReminderSort.BY_EARLIEST_DATE_FIRST
        )

        composeTestRule.apply {
            val reminders = onNodeWithTag("test_tag_reminder_list_lazy_column")
                .onChildren()

            reminders[0].assertTextContains("Test Overdue Reminder")
            reminders[1].assertTextContains("Test Scheduled Reminder")
        }
    }

    @Test
    fun when_loading_reminder_list_active_should_display_scaffold_with_blank_list() {
        setContent(
            isLoading = true
        )

        composeTestRule.apply {
            onNodeWithText("RemindMe").assertIsDisplayed()
            onNodeWithContentDescription("Sort reminders").assertIsDisplayed()
            onNodeWithContentDescription("Completed reminders").assertIsDisplayed()
            onNodeWithContentDescription("Search reminders").assertIsDisplayed()
            onNodeWithContentDescription("Add reminder").assertIsDisplayed()

            onNodeWithText("Test Overdue Reminder").assertDoesNotExist()
            onNodeWithText("Sat, 01 Jan 2000 • 08:00").assertDoesNotExist()
            onNodeWithText("Overdue").assertDoesNotExist()

            onNodeWithText("Test Scheduled Reminder").assertDoesNotExist()
            onNodeWithText("Wed, 01 Jan 3000 • 09:00").assertDoesNotExist()
            onNodeWithText("Scheduled").assertDoesNotExist()
        }
    }

    @Test
    fun when_reminder_list_active_is_empty_should_display_empty_state() {
        setContent(
            activeReminderStates = emptyList()
        )

        composeTestRule.apply {
            onNodeWithText("No reminders").assertIsDisplayed()
            onNodeWithText("Add a reminder to get started").assertIsDisplayed()
        }
    }
}

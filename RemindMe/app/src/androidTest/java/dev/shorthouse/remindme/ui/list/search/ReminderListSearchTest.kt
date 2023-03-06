package dev.shorthouse.remindme.ui.list.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.shorthouse.remindme.HiltTestActivity
import dev.shorthouse.remindme.ui.screen.list.search.ReminderListSearchScaffold
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.theme.RemindMeTheme
import dev.shorthouse.remindme.util.ReminderTestUtil
import java.time.LocalTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ReminderListSearchTest {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltTestRule.inject()
    }

    private fun setContent(
        searchReminderStates: List<ReminderState> = listOf(
            ReminderTestUtil().createReminderState(
                name = "Test Search Reminder",
                date = "Wed, 01 Jan 2020",
                time = LocalTime.parse("08:00"),
                isCompleted = true
            )
        ),
        searchQuery: String = "",
        isLoading: Boolean = false
    ) {
        composeTestRule.setContent {
            RemindMeTheme {
                ReminderListSearchScaffold(
                    searchReminderStates = searchReminderStates,
                    isLoading = isLoading,
                    searchQuery = searchQuery,
                    onNavigateUp = {},
                    onSearchQueryChange = {},
                    onClearSearchQuery = {},
                    onReminderCard = {}
                )
            }
        }
    }

    @Test
    fun when_reminder_list_search_created_should_display_expected_scaffold_content() {
        setContent(
            searchReminderStates = emptyList()
        )

        composeTestRule.apply {
            onNodeWithContentDescription("Back").assertIsDisplayed()
            onNodeWithText("Search...").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_list_search_created_should_display_expected_list_content() {
        setContent()

        composeTestRule.apply {
            onNodeWithText("Test Search Reminder").assertIsDisplayed()
            onNodeWithText("Wed, 01 Jan 2020 • 08:00").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_list_search_query_is_populated_should_display_in_search_bar_and_hide_hint() {
        setContent(
            searchQuery = "SearchQuery"
        )

        composeTestRule.apply {
            onNodeWithText("SearchQuery").assertIsDisplayed()
            onNodeWithText("Search...").assertDoesNotExist()
        }
    }

    @Test
    fun when_loading_reminder_list_search_should_display_scaffold_with_blank_list() {
        setContent(
            isLoading = true
        )

        composeTestRule.apply {
            onNodeWithContentDescription("Back").assertIsDisplayed()
            onNodeWithText("Search...").assertIsDisplayed()

            onNodeWithText("Test Search Reminder").assertDoesNotExist()
            onNodeWithText("Wed, 01 Jan 2020 • 08:00").assertDoesNotExist()
        }
    }

    @Test
    fun when_reminder_list_search_is_empty_should_display_empty_state() {
        setContent(
            searchReminderStates = emptyList()
        )

        composeTestRule.apply {
            onNodeWithText("No reminders found").assertIsDisplayed()
            onNodeWithText("Try adjusting your search").assertIsDisplayed()
        }
    }
}

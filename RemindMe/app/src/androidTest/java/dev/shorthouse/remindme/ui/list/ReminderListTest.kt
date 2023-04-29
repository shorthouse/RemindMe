package dev.shorthouse.remindme.ui.list

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
import dev.shorthouse.remindme.data.protodatastore.ReminderFilter
import dev.shorthouse.remindme.model.RepeatInterval
import dev.shorthouse.remindme.ui.theme.AppTheme
import dev.shorthouse.remindme.util.ReminderTestUtil
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ReminderListTest {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setup() {
        hiltTestRule.inject()
    }

    private fun setContent(
        uiState: ListUiState = ListUiState()
    ) {
        composeTestRule.setContent {
            AppTheme {
                ReminderListScaffold(
                    uiState = uiState,
                    onApplyFilter = {},
                    onApplySort = {},
                    onSearch = {},
                    onSearchQueryChange = {},
                    onCloseSearch = {},
                    onNavigateAdd = {},
                    onDismissBottomSheet = {},
                    onNavigateDetails = {},
                    onReminderActionSelected = {}
                )
            }
        }
    }

    @Test
    fun when_reminder_list_created_should_display_expected_content() {
        setContent()

        composeTestRule.apply {
            onNodeWithText("RemindMe").assertIsDisplayed()
            onNodeWithContentDescription("Sort reminders").assertIsDisplayed()
            onNodeWithContentDescription("Search reminders").assertIsDisplayed()

            onNodeWithText("Overdue").assertIsDisplayed()
            onNodeWithText("Upcoming").assertIsDisplayed()
            onNodeWithText("Completed").assertIsDisplayed()

            onNodeWithContentDescription("Add reminder").assertIsDisplayed()
        }
    }

    @Test
    fun when_overdue_reminder_list_empty_should_display_empty_state() {
        setContent(
            uiState = ListUiState(
                reminders = emptyList(),
                reminderFilter = ReminderFilter.OVERDUE
            )
        )

        composeTestRule.apply {
            onNodeWithText("No overdue reminders").assertIsDisplayed()
            onNodeWithText("You're all caught up").assertIsDisplayed()
        }
    }

    @Test
    fun when_upcoming_reminder_list_empty_should_display_empty_state() {
        setContent(
            uiState = ListUiState(
                reminders = emptyList(),
                reminderFilter = ReminderFilter.UPCOMING
            )
        )

        composeTestRule.apply {
            onNodeWithText("No upcoming reminders").assertIsDisplayed()
            onNodeWithText("Add a reminder to get started").assertIsDisplayed()
        }
    }

    @Test
    fun when_completed_reminder_list_empty_should_display_empty_state() {
        setContent(
            uiState = ListUiState(
                reminders = emptyList(),
                reminderFilter = ReminderFilter.COMPLETED
            )
        )

        composeTestRule.apply {
            onNodeWithText("No completed reminders").assertIsDisplayed()
            onNodeWithText("Completed reminders will show up here").assertIsDisplayed()
        }
    }

    @Test
    fun when_overdue_reminder_exists_should_display_overdue_reminder() {
        setContent(
            uiState = ListUiState(
                reminders = listOf(
                    ReminderTestUtil().createReminder(
                        name = "Overdue reminder",
                        startDateTime = ZonedDateTime.now().minusDays(1)
                    )
                ),
                reminderFilter = ReminderFilter.OVERDUE
            )
        )

        composeTestRule.apply {
            onNodeWithText("Overdue reminder").assertIsDisplayed()
        }
    }

    @Test
    fun when_upcoming_reminder_exists_should_display_upcoming_reminder() {
        setContent(
            uiState = ListUiState(
                reminders = listOf(
                    ReminderTestUtil().createReminder(
                        name = "Upcoming reminder",
                        startDateTime = ZonedDateTime.now().plusDays(1)
                    )
                ),
                reminderFilter = ReminderFilter.UPCOMING
            )
        )

        composeTestRule.apply {
            onNodeWithText("Upcoming reminder").assertIsDisplayed()
        }
    }

    @Test
    fun when_completed_reminder_exists_should_display_completed_reminder() {
        setContent(
            uiState = ListUiState(
                reminders = listOf(
                    ReminderTestUtil().createReminder(
                        name = "Completed reminder",
                        isCompleted = true
                    )
                ),
                reminderFilter = ReminderFilter.COMPLETED
            )
        )

        composeTestRule.apply {
            onNodeWithText("Completed reminder").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_card_displayed_in_list_should_have_expected_content() {
        setContent(
            uiState = ListUiState(
                reminders = listOf(
                    ReminderTestUtil().createReminder(
                        name = "Test reminder",
                        startDateTime = ZonedDateTime.parse("2020-01-01T08:30:00Z"),
                        repeatInterval = RepeatInterval(1, ChronoUnit.DAYS),
                        notes = "notes",
                        isNotificationSent = true
                    )
                )
            )
        )

        composeTestRule.apply {
            onNodeWithText("Test reminder").assertIsDisplayed()
            onNodeWithText("Wed, 01 Jan 2020 • 08:30").assertIsDisplayed()
            onNodeWithContentDescription("Notification").assertIsDisplayed()
            onNodeWithContentDescription("Repeat Interval").assertIsDisplayed()
            onNodeWithText("1 day").assertIsDisplayed()
            onNodeWithContentDescription("Notes").assertIsDisplayed()
            onNodeWithText("notes").assertIsDisplayed()
        }
    }

    @Test
    fun when_sort_icon_clicked_should_display_sort_dialog() {
        setContent()

        composeTestRule.apply {
            onNodeWithContentDescription("Sort reminders").performClick()

            onNodeWithText("Sort reminders by").assertIsDisplayed()
            onNodeWithText("Date - Earliest first").assertIsDisplayed()
            onNodeWithText("Date - Latest first").assertIsDisplayed()
            onNodeWithText("Alphabetical A-Z").assertIsDisplayed()
            onNodeWithText("Alphabetical Z-A").assertIsDisplayed()
            onNodeWithText("Apply").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminders_sorted_by_date_earliest_first_should_display_in_expected_order() {
        setContent(
            uiState = ListUiState(
                reminders = listOf(
                    ReminderTestUtil().createReminder(
                        name = "Earlier reminder",
                        startDateTime = ZonedDateTime.now().minusDays(1)
                    ),
                    ReminderTestUtil().createReminder(
                        name = "Later reminder",
                        startDateTime = ZonedDateTime.now().plusDays(1)
                    )
                )
            )
        )

        composeTestRule.apply {
            onNodeWithContentDescription("Sort reminders").performClick()
            onNodeWithText("Date - Earliest first").performClick()
            onNodeWithText("Apply").performClick()
            waitForIdle()

            val reminders = onNodeWithTag("test_tag_reminder_list_lazy_column").onChildren()

            reminders[0].assertTextContains("Earlier reminder")
            reminders[1].assertTextContains("Later reminder")
        }
    }

    @Test
    fun when_reminders_sorted_by_date_latest_first_should_display_in_expected_order() {
        setContent(
            uiState = ListUiState(
                reminders = listOf(
                    ReminderTestUtil().createReminder(
                        name = "Later reminder",
                        startDateTime = ZonedDateTime.now().minusDays(1)
                    ),
                    ReminderTestUtil().createReminder(
                        name = "Earlier reminder",
                        startDateTime = ZonedDateTime.now().plusDays(1)
                    )
                )
            )
        )

        composeTestRule.apply {
            onNodeWithContentDescription("Sort reminders").performClick()
            onNodeWithText("Date - Latest first").performClick()
            onNodeWithText("Apply").performClick()
            waitForIdle()

            val reminders = onNodeWithTag("test_tag_reminder_list_lazy_column").onChildren()

            reminders[0].assertTextContains("Later reminder")
            reminders[1].assertTextContains("Earlier reminder")
        }
    }
}

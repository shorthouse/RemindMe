package dev.shorthouse.remindme.compose

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import dev.shorthouse.remindme.FakeDataSource
import dev.shorthouse.remindme.HiltTestActivity
import dev.shorthouse.remindme.compose.screen.ReminderListHomeScreen
import dev.shorthouse.remindme.data.ReminderDataSource
import dev.shorthouse.remindme.di.DataSourceModule
import dev.shorthouse.remindme.theme.RemindMeTheme
import dev.shorthouse.remindme.util.TestUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.ZonedDateTime
import javax.inject.Singleton

@HiltAndroidTest
@UninstallModules(DataSourceModule::class)
class ReminderListHomeTest {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Module
    @InstallIn(SingletonComponent::class)
    class TestModule {
        @Singleton
        @Provides
        fun provideReminderDataSource(): ReminderDataSource {
            val reminders = mutableListOf(
                TestUtil().createReminder(
                    name = "Test Overdue Reminder",
                    startDateTime = ZonedDateTime.parse("2000-01-01T08:00:00Z")
                ),
                TestUtil().createReminder(
                    name = "Test Scheduled Reminder",
                    startDateTime = ZonedDateTime.parse("3000-01-01T09:00:00Z")
                ),
                TestUtil().createReminder(
                    name = "Test Completed Reminder",
                    startDateTime = ZonedDateTime.parse("1000-01-01T10:00:00Z"),
                    isCompleted = true
                )
            )

            return FakeDataSource(reminders)
        }
    }

    @Before
    fun setup() {
        hiltTestRule.inject()
    }

    private fun setContent() {
        composeTestRule.setContent {
            RemindMeTheme {
                ReminderListHomeScreen(
                    navigator = EmptyDestinationsNavigator
                )
            }
        }
    }

    @Test
    fun when_reminder_list_home_created_should_display_expected_content() {
        setContent()

        composeTestRule.apply {
            onNodeWithTag("Overdue Reminders title").assertIsDisplayed()
            onNodeWithTag("Scheduled Reminders title").assertDoesNotExist()
            onNodeWithTag("Completed Reminders title").assertDoesNotExist()

            onNodeWithContentDescription("Menu").assertIsDisplayed()
            onNodeWithContentDescription("Sort").assertIsDisplayed()
            onNodeWithContentDescription("Add reminder").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_navigation_menu_clicked_should_display_expected_bottom_sheet() {
        setContent()

        composeTestRule.apply {
            onNodeWithContentDescription("Menu").performClick()

            onNodeWithText("RemindMe").assertIsDisplayed()
            onNodeWithTag("Overdue Reminders").assertIsDisplayed()
            onNodeWithTag("Scheduled Reminders").assertIsDisplayed()
            onNodeWithTag("Completed Reminders").assertIsDisplayed()
        }
    }

    @Test
    fun when_overdue_reminders_selected_should_display_expected_list() {
        setContent()

        composeTestRule.apply {
            onNodeWithContentDescription("Menu").performClick()
            onNodeWithTag("Overdue Reminders").performClick()
            onNodeWithTag("Overdue Reminders title").assertIsDisplayed()

            onNodeWithText("Test Overdue Reminder").assertIsDisplayed()
            onNodeWithText("Sat, 01 Jan 2000").assertIsDisplayed()
            onNodeWithText("08:00").assertIsDisplayed()
            onNodeWithContentDescription("Complete reminder").assertIsDisplayed()
        }
    }

    @Test
    fun when_scheduled_reminders_selected_should_display_expected_list() {
        setContent()

        composeTestRule.apply {
            onNodeWithContentDescription("Menu").performClick()
            onNodeWithTag("Scheduled Reminders").performClick()
            onNodeWithTag("Scheduled Reminders title").assertIsDisplayed()

            onNodeWithText("Test Scheduled Reminder").assertIsDisplayed()
            onNodeWithText("Wed, 01 Jan 3000").assertIsDisplayed()
            onNodeWithText("09:00").assertIsDisplayed()
        }
    }

    @Test
    fun when_completed_reminders_selected_should_display_expected_list() {
        setContent()

        composeTestRule.apply {
            onNodeWithContentDescription("Menu").performClick()
            onNodeWithTag("Completed Reminders").performClick()
            onNodeWithTag("Completed Reminders title").assertIsDisplayed()

            onNodeWithText("Test Completed Reminder").assertIsDisplayed()
            onNodeWithText("Wed, 01 Jan 1000").assertIsDisplayed()
            onNodeWithText("10:00").assertIsDisplayed()
        }
    }

    @Test
    fun when_reminder_sort_clicked_should_display_expected_bottom_sheet() {
        setContent()

        composeTestRule.apply {
            onNodeWithContentDescription("Sort").performClick()

            onNodeWithText("Sort by").assertIsDisplayed()
            onNodeWithText("Earliest date first").assertIsDisplayed()
            onNodeWithText("Latest date first").assertIsDisplayed()
        }
    }
}

package dev.shorthouse.remindme.ui.list.active

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.test.core.app.ApplicationProvider
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dev.shorthouse.remindme.HiltTestActivity
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesSerializer
import dev.shorthouse.remindme.ui.screen.list.active.ListActiveViewModel
import dev.shorthouse.remindme.ui.screen.list.active.ReminderListActiveScreen
import dev.shorthouse.remindme.ui.theme.RemindMeTheme
import dev.shorthouse.remindme.util.ReminderTestUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.ZonedDateTime

@HiltAndroidTest
@OptIn(ExperimentalCoroutinesApi::class)
class ReminderListActiveTest {
    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    private val testCoroutineDispatcher = StandardTestDispatcher()

    private val testDataStore =
        DataStoreFactory.create(
            //scope = TestScope(testCoroutineDispatcher + Job()),
            serializer = UserPreferencesSerializer,
            produceFile = {
                ApplicationProvider.getApplicationContext<HiltTestApplication>().dataStoreFile("test_datastore")
            },
            corruptionHandler = null
        )

    private val userPreferencesRepository = UserPreferencesRepository(testDataStore)

    @Before
    fun setup() {
        hiltTestRule.inject()
    }

    private fun setContent() {
        composeTestRule.setContent {
            RemindMeTheme {

                val fakeReminderDataSource = FakeDataSource(
                    mutableListOf(
                        ReminderTestUtil().createReminder(
                            name = "Test Overdue Reminder",
                            startDateTime = ZonedDateTime.parse("2000-01-01T08:00:00Z")
                        ),
                        ReminderTestUtil().createReminder(
                            name = "Test Scheduled Reminder",
                            startDateTime = ZonedDateTime.parse("3000-01-01T09:00:00Z")
                        ),
                        ReminderTestUtil().createReminder(
                            name = "Test Completed Reminder",
                            startDateTime = ZonedDateTime.parse("1000-01-01T10:00:00Z"),
                            isCompleted = true
                        )
                    )
                )

                val reminderRepository = ReminderRepository(fakeReminderDataSource)

                val listActiveViewModel = ListActiveViewModel(
                    reminderRepository = reminderRepository,
                    userPreferencesRepository = userPreferencesRepository,
                    ioDispatcher = testCoroutineDispatcher
                )

                ReminderListActiveScreen(
                    listActiveViewModel = listActiveViewModel,
                    navigator = EmptyDestinationsNavigator
                )
            }
        }
    }

    @Test
    fun when_reminder_list_active_created_should_display_expected_scaffold_content() {
        setContent()

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
            onNodeWithText("Sat, 01 Jan 2000").assertIsDisplayed()
            onNodeWithText("08:00").assertIsDisplayed()
            onNodeWithText("Overdue").assertIsDisplayed()

            onNodeWithText("Test Scheduled Reminder").assertIsDisplayed()
            onNodeWithText("Wed, 01 Jan 3000").assertIsDisplayed()
            onNodeWithText("09:00").assertIsDisplayed()
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
    fun when_reminders_sorted_by_date_earliest_first_should_display_in_expected_order() {
        setContent()

        composeTestRule.apply {
            onNodeWithContentDescription("Sort reminders").performClick()

            onNodeWithText("Date (Earliest first)").performClick()
            onNodeWithText("Apply").performClick()

            val reminders = onNodeWithTag("test_tag_reminder_list_lazy_column")
                .onChildren()

            reminders[0].assertTextContains("Overdue")
            reminders[0].assertTextContains("Scheduled")
        }
    }

    @Test
    fun when_reminders_sorted_by_date_latest_first_should_display_in_expected_order() {
        setContent()

        composeTestRule.apply {
            onNodeWithContentDescription("Sort reminders").performClick()

            onNodeWithText("Date (Latest first)").performClick()
            onNodeWithText("Apply").performClick()

            val reminders = onNodeWithTag("test_tag_reminder_list_lazy_column")
                .onChildren()

            reminders[0].assertTextContains("Scheduled")
            reminders[0].assertTextContains("Overdue")
        }
    }
}

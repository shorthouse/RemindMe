package dev.shorthouse.remindme.compose

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.shorthouse.remindme.HiltTestActivity
import dev.shorthouse.remindme.MainActivity
import dev.shorthouse.remindme.model.RepeatInterval
import dev.shorthouse.remindme.util.TestUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@HiltAndroidTest
class ReminderDetailsTest {

    @get:Rule(order = 1)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    private val testReminders = listOf(
        TestUtil.createReminder(
            id = 1L,
            name = "Test Reminder Details",
            startDateTime = ZonedDateTime.parse("2000-01-01T14:02:00Z")
        ),
        TestUtil.createReminder(
            id = 2L,
            name = "Test Reminder Details with Repeat Interval",
            startDateTime = ZonedDateTime.parse("2000-01-01T14:02:00Z"),
            repeatInterval = RepeatInterval(2, ChronoUnit.WEEKS)
        ),
        TestUtil.createReminder(
            id = 3L,
            name = "Test Reminder Details with Notes",
            notes = "notes"
        ),
        TestUtil.createReminder(
            id = 4L,
            name = "Test Reminder Details with Notification Enabled",
            isNotificationSent = true
        ),
        TestUtil.createReminder(
            id = 5L,
            name = "Test Reminder Details with Everything",
            startDateTime = ZonedDateTime.parse("2000-01-01T14:02:00Z"),
            repeatInterval = RepeatInterval(2, ChronoUnit.WEEKS),
            isNotificationSent = true,
            notes = "notes"
        ),
        TestUtil.createReminder(
            id = 6L,
            name = "m".repeat(200)
        ),
        TestUtil.createReminder(
            id = 7L,
            name = "Test Reminder Large Notes",
            notes = "m".repeat(1000),
            isNotificationSent = true
        )
    )

    @Before
    fun setup() {
        hiltTestRule.inject()
    }

    @Test
    fun when_reminder_details_created_should_display_top_app_bar() {
        composeTestRule.setContent {
            MdcTheme {
                ReminderDetailsScreenContent(
                    detailsViewModel = hiltViewModel(),
                    reminder = testReminders[0].createDisplayReminder(),
                    onNavigateEdit = {},
                    onNavigateUp = {}
                )
            }
        }

        composeTestRule.apply {
            onNodeWithText("Details").assertIsDisplayed()
            onNodeWithContentDescription("Back").assertIsDisplayed()
            onNodeWithContentDescription("Edit").assertIsDisplayed()
            onNodeWithContentDescription("More").assertIsDisplayed()

            onNodeWithContentDescription("More").performClick()
            onNodeWithText("Complete").assertIsDisplayed()
            onNodeWithText("Delete").assertIsDisplayed()
        }
    }
}
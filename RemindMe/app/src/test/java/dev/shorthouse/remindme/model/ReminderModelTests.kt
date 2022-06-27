package dev.shorthouse.remindme.model

import dev.shorthouse.remindme.data.RepeatInterval
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class ReminderModelTests {
    private lateinit var reminder: Reminder
    private lateinit var oneOffReminder: Reminder

    @Before
    fun setUp() {
        oneOffReminder = Reminder(
            id = 1,
            name = "oneOffReminder",
            startDateTime = ZonedDateTime.now(),
            repeatInterval = null,
            notes = null,
            isArchived = false,
            isNotificationSent = false
        )

        reminder = Reminder(
            id = 1,
            name = "repeatReminder",
            startDateTime = ZonedDateTime.of(
                2000,
                6,
                15,
                19,
                1,
                0,
                0,
                ZoneId.of("Europe/London")
            ),
            repeatInterval = RepeatInterval(1, ChronoUnit.DAYS),
            notes = "notes",
            isArchived = false,
            isNotificationSent = true
        )
    }

    @Test
    fun reminderModel_OneOffReminder_ReturnsFalse() {
        assertFalse(oneOffReminder.isRepeatReminder())
    }

    @Test
    fun reminderModel_RepeatReminder_ReturnsTrue() {
        assertTrue(reminder.isRepeatReminder())
    }

    @Test
    fun reminderModel_FormattedStartDate_FormatsCorrectly() {
        assertEquals(reminder.getFormattedStartDate(), "15 Jun 2000")
    }

    @Test
    fun reminderModel_FormattedStartTime_FormatsCorrectly() {
        assertEquals(reminder.getFormattedStartTime(), "19:01")
    }
}
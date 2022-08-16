package dev.shorthouse.remindme.model

import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.data.RepeatInterval
import org.junit.Before
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class ReminderTest {
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
    fun `is repeat reminder on repeat reminder, returns true`() {
        val isRepeatReminder = reminder.isRepeatReminder()

        assertThat(isRepeatReminder).isTrue()
    }

    @Test
    fun `is repeat reminder on one off reminder, returns false`() {
        val isRepeatReminder = oneOffReminder.isRepeatReminder()

        assertThat(isRepeatReminder).isFalse()
    }

    @Test
    fun `get formatted start date, returns expected formatted start date`() {
        val expectedDate = "15 Jun 2000"

        val date = reminder.getFormattedStartDate()

        assertThat(date).isEqualTo(expectedDate)
    }

    @Test
    fun `get formatted start time, returns expected formatted start time`() {
        val expectedTime = "19:01"

        val time = reminder.getFormattedStartTime()

        assertThat(time).isEqualTo(expectedTime)
    }
}
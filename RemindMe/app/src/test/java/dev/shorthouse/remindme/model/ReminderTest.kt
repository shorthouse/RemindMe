package dev.shorthouse.remindme.model

import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.util.ReminderTestUtil
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import org.junit.Before
import org.junit.Test

class ReminderTest {
    private lateinit var repeatReminder: Reminder
    private lateinit var oneOffReminder: Reminder

    @Before
    fun setUp() {
        repeatReminder = ReminderTestUtil().createReminder(
            id = 1L,
            name = "repeatReminder",
            startDateTime = ZonedDateTime.parse("2000-06-15T19:01:00Z"),
            repeatInterval = RepeatInterval(1, ChronoUnit.DAYS)
        )

        oneOffReminder = ReminderTestUtil().createReminder(
            id = 2L,
            name = "oneOffReminder",
            startDateTime = ZonedDateTime.now()
        )
    }

    @Test
    fun `Is repeat reminder on repeat reminder, returns true`() {
        val isRepeatReminder = repeatReminder.isRepeatReminder()

        assertThat(isRepeatReminder).isTrue()
    }

    @Test
    fun `Is repeat reminder on one off reminder, returns false`() {
        val isRepeatReminder = oneOffReminder.isRepeatReminder()

        assertThat(isRepeatReminder).isFalse()
    }

    @Test
    fun `Get formatted start time, returns expected formatted start time`() {
        val expectedTime = "19:01"

        val time = repeatReminder.getFormattedStartTime()

        assertThat(time).isEqualTo(expectedTime)
    }
}

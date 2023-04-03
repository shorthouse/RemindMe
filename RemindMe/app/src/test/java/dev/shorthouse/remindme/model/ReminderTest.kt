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
    private lateinit var overdueReminder: Reminder
    private lateinit var nonOverdueReminder: Reminder

    @Before
    fun setup() {
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

        overdueReminder = ReminderTestUtil().createReminder(
            id = 3L,
            name = "overdueReminder",
            startDateTime = ZonedDateTime.parse("2000-01-01T00:00:00Z")
        )

        nonOverdueReminder = ReminderTestUtil().createReminder(
            id = 4L,
            name = "upcomingReminder",
            startDateTime = ZonedDateTime.parse("3000-01-01T00:00:00Z")
        )
    }

    @Test
    fun `Get formatted date, returns correctly formatted date`() {
        val expectedDate = "Thu, 15 Jun 2000"

        val date = repeatReminder.getFormattedDate()

        assertThat(date).isEqualTo(expectedDate)
    }

    @Test
    fun `Get formatted time, returns correctly formatted time`() {
        val expectedTime = "19:01"

        val time = repeatReminder.getFormattedTime()

        assertThat(time).isEqualTo(expectedTime)
    }

    @Test
    fun `Is repeat reminder on one off reminder, returns false`() {
        val isRepeatReminder = oneOffReminder.isRepeatReminder()

        assertThat(isRepeatReminder).isFalse()
    }

    @Test
    fun `Is repeat reminder on repeat reminder, returns true`() {
        val isRepeatReminder = repeatReminder.isRepeatReminder()

        assertThat(isRepeatReminder).isTrue()
    }

    @Test
    fun `Is overdue on non overdue reminder, returns false`() {
        val isOverdue = nonOverdueReminder.isOverdue()

        assertThat(isOverdue).isFalse()
    }

    @Test
    fun `Is overdue on overdue reminder, returns true`() {
        val isOverdue = overdueReminder.isOverdue()

        assertThat(isOverdue).isTrue()
    }
}

package dev.shorthouse.remindme.viewmodel

import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.RepeatInterval
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.NotificationScheduler
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class AddEditViewModelTest {
    // Class under test
    private lateinit var viewModel: AddEditViewModel

    @MockK
    private lateinit var notificationScheduler: NotificationScheduler

    private val reminder = Reminder(
        id = 1,
        name = "repeatActiveReminder",
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
        repeatInterval = RepeatInterval(4, ChronoUnit.WEEKS),
        notes = "notes",
        isArchived = false,
        isNotificationSent = true
    )

    private val oneOffReminder = Reminder(
        id = 2,
        name = "oneOffReminder",
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
        null,
        notes = null,
        isArchived = false,
        isNotificationSent = false
    )

    private val reminders = mutableListOf(reminder, oneOffReminder)

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        val dataSource = FakeDataSource()
        val reminderRepository = ReminderRepository(dataSource)
        viewModel = AddEditViewModel(reminderRepository, notificationScheduler)
    }

    @Test
    fun `format reminder start date, returns correct date`() {
        val expectedDate = "Thu, 15 Jun 2000"
        val date = viewModel.getFormattedDate(reminder.startDateTime)

        assertThat(date).isEqualTo(expectedDate)
    }

    @Test
    fun `format reminder start time, returns correct time`() {
        val expectedTime = "19:01"
        val time = viewModel.getFormattedTime(reminder.startDateTime)

        assertThat(time).isEqualTo(expectedTime)
    }

    @Test
    fun `get start time next hour, returns correct time`() {
        val expectedTime = "20:00"
        val time = viewModel.getFormattedTimeNextHour(reminder.startDateTime)

        assertThat(time).isEqualTo(expectedTime)
    }

    @Test
    fun `get reminder notes that exist, returns notes`() {
        val expectedNotes = "notes"
        val notes = viewModel.getReminderNotes(reminder)

        assertThat(notes).isEqualTo(expectedNotes)
    }

    @Test
    fun `get reminder notes that are null, returns empty string`() {
        val expectedNotes = ""
        val notes = viewModel.getReminderNotes(oneOffReminder)

        assertThat(notes).isEqualTo(expectedNotes)
    }

    @Test
    fun `get repeat value on repeat reminder, returns expected repeat value`() {
        val expectedRepeatValue = "4"
        val repeatValue = viewModel.getRepeatValue(reminder)

        assertThat(repeatValue).isEqualTo(expectedRepeatValue)
    }

    @Test
    fun `get repeat value on one off reminder, returns default repeat value`() {
        val expectedRepeatValue = "1"
        val repeatValue = viewModel.getRepeatValue(oneOffReminder)

        assertThat(repeatValue).isEqualTo(expectedRepeatValue)
    }

    @Test
    fun `get repeat unit on repeat reminder, returns expected repeat unit`() {
        val expectedRepeatUnit = ChronoUnit.WEEKS
        val repeatUnit = viewModel.getRepeatUnit(reminder)

        assertThat(repeatUnit).isEqualTo(expectedRepeatUnit)
    }

    @Test
    fun `get repeat unit on one off reminder, returns default repeat unit`() {
        val expectedRepeatUnit = ChronoUnit.DAYS
        val repeatUnit = viewModel.getRepeatUnit(oneOffReminder)

        assertThat(repeatUnit).isEqualTo(expectedRepeatUnit)
    }

    @Test
    fun `get is notification sent on notifying reminder, should return true`() {
        val isNotificationSent = viewModel.getIsNotificationSent(reminder)

        assertThat(isNotificationSent).isTrue()
    }

    @Test
    fun `get is notification sent on non notifying reminder, should return false`() {
        val isNotificationSent = viewModel.getIsNotificationSent(oneOffReminder)

        assertThat(isNotificationSent).isFalse()
    }

    @Test
    fun `convert date time string to ZonedDateTime, returns expected date time`() {
        val dateText = "Thu, 15 Jun 2000"
        val timeText = "19:01"
        val expectedDateTime = reminder.startDateTime

        val dateTime = viewModel.convertDateTimeStringToDateTime(dateText, timeText)

        assertThat(dateTime).isEqualTo(expectedDateTime)
    }

    @Test
    fun `format date picker date, returns expected formatted date`() {
        val timeStampMillis = 100_000_000L
        val expectedDate = "Fri, 02 Jan 1970"

        val date = viewModel.convertEpochMilliToDate(timeStampMillis)

        assertThat(date).isEqualTo(expectedDate)
    }

    @Test
    fun `format time picker time, returns expected formatted time`() {
        val hour = 14
        val minute = 30
        val expectedTime = "14:30"

        val time = viewModel.formatTimePickerTime(hour, minute)

        assertThat(time).isEqualTo(expectedTime)
    }

    @Test
    fun `get repeat interval with valid interval, returns expected interval`() {
        val timeValue = 10L
        val repeatUnitString = "weeks"

        val expectedRepeatInterval = RepeatInterval(10L, ChronoUnit.WEEKS)

        val repeatInterval = viewModel.getRepeatInterval(timeValue, repeatUnitString)

        assertThat(repeatInterval).isEqualTo(expectedRepeatInterval)
    }

    @Test
    fun `get repeat interval with null interval, returns null`() {
        val timeValue = 0L
        val repeatUnitString = ""

        val repeatInterval = viewModel.getRepeatInterval(timeValue, repeatUnitString)

        assertThat(repeatInterval).isNull()
    }

    @Test
    fun `get reminder notes with non blank notes, returns notes`() {
        val notesInput = "notes"

        val notes = viewModel.getReminderNotes(notesInput)

        assertThat(notes).isEqualTo(notesInput)
    }

    @Test
    fun `get reminder notes with blank notes, returns null`() {
        val notes = viewModel.getReminderNotes("")

        assertThat(notes).isNull()
    }

    @Test
    fun `is name valid with non blank name, returns true`() {
        val name = "name"

        val isNameValid = viewModel.isNameValid(name)

        assertThat(isNameValid).isTrue()
    }

    @Test
    fun `is name valid with blank name, returns false`() {
        val name = "    "

        val isNameValid = viewModel.isNameValid(name)

        assertThat(isNameValid).isFalse()
    }

    @Test
    fun `is repeat interval valid with greater than 0 repeat interval value, returns true`() {
        val repeatIntervalValue = 10L

        val isRepeatIntervalValid = viewModel.isRepeatIntervalValid(repeatIntervalValue)

        assertThat(isRepeatIntervalValid).isTrue()
    }

    @Test
    fun `is repeat interval valid with 0 repeat interval value, returns false`() {
        val repeatIntervalValue = 0L

        val isRepeatIntervalValid = viewModel.isRepeatIntervalValid(repeatIntervalValue)

        assertThat(isRepeatIntervalValid).isFalse()
    }

    @Test
    fun `is repeat interval valid with negative repeat interval value, returns false`() {
        val repeatIntervalValue = -10L

        val isRepeatIntervalValid = viewModel.isRepeatIntervalValid(repeatIntervalValue)

        assertThat(isRepeatIntervalValid).isFalse()
    }
}
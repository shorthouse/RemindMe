package dev.shorthouse.remindme.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.RepeatInterval
import dev.shorthouse.remindme.getOrAwaitValue
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.RemindersSort
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@RunWith(AndroidJUnit4::class)
class ActiveReminderListViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Class under test
    private lateinit var viewModel: ActiveReminderListViewModel

    private lateinit var reminders: MutableList<Reminder>

    @Before
    fun setup() {
        val reminderOlder = Reminder(
            id = 1,
            name = "reminder1",
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
            repeatInterval = null,
            notes = "notes",
            isArchived = false,
            isNotificationSent = true
        )

        val reminderNewer = Reminder(
            id = 2,
            name = "reminder2",
            startDateTime = ZonedDateTime.of(
                3000,
                6,
                15,
                19,
                1,
                0,
                0,
                ZoneId.of("Europe/London")
            ),
            repeatInterval = RepeatInterval(2, ChronoUnit.WEEKS),
            notes = "notes",
            isArchived = false,
            isNotificationSent = true
        )

        val reminderNow = Reminder(
            id = 3,
            name = "reminder3",
            startDateTime = ZonedDateTime.now(),
            repeatInterval = RepeatInterval(1, ChronoUnit.DAYS),
            notes = "notes",
            isArchived = false,
            isNotificationSent = true
        )

        reminders = mutableListOf(reminderOlder, reminderNow, reminderNewer)

        val dataSource = FakeDataSource(reminders)
        val reminderRepository = ReminderRepository(dataSource)
        viewModel = ActiveReminderListViewModel(reminderRepository)
    }

    @Test
    fun `filter on current time, returns only active reminders`() {
        val sort = MutableLiveData(RemindersSort.NEWEST_FIRST)
        val filter = MutableLiveData("")

        val filterTimeReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()

        assertThat(filterTimeReminders).isEqualTo(reminders.subList(0, 1))
    }

    @Test
    fun `update current time, updates to correct time`() {
        val nowBefore = ZonedDateTime.now()
        viewModel.updateCurrentTime()
        val nowAfter = ZonedDateTime.now()

        val currentTime = viewModel.currentTime.getOrAwaitValue()

        assertThat(currentTime.isAfter(nowBefore))
        assertThat(currentTime.isBefore(nowAfter))
    }

    @Test
    fun `get millis until next minute, returns correct value`() {
        val currentSeconds = 32

        val dateTime = LocalDateTime.of(
            2000, 1, 1, 1, 1, currentSeconds, 0
        )

        val millisUntilNextMinuteExpected = (60 - currentSeconds) * 1000

        val millisUntilNextMinute = viewModel.getMillisUntilNextMinute(dateTime)

        assertThat(millisUntilNextMinute).isEqualTo(millisUntilNextMinuteExpected)
    }

    @Test
    fun `set one-off reminder to done, should archive`() {
        val reminder = reminders.first()
        viewModel.updateDoneReminder(reminder)

        val updatedDoneReminder = viewModel.repository.getReminder(reminder.id).asLiveData().getOrAwaitValue()

        assertThat(updatedDoneReminder.isArchived)
    }

    @Test
    fun `set repeat reminder to done, should not archive`() {
        val reminder = reminders.last()
        viewModel.updateDoneReminder(reminder)

        val updatedDoneReminder = viewModel.repository.getReminder(reminder.id).asLiveData().getOrAwaitValue()

        assertThat(!updatedDoneReminder.isArchived)
    }

    @Test
    fun `update repeat reminder, should correctly update start date time`() {
        val reminder = reminders[1]
        val expectedUpdatedDateTime = reminder.startDateTime.plusDays(1)

        val updatedDoneReminder = viewModel.getUpdatedDoneReminder(reminder)

        assertThat(updatedDoneReminder.startDateTime).isEqualTo(expectedUpdatedDateTime)
    }
}
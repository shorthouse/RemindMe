package dev.shorthouse.remindme.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.model.RepeatInterval
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.util.TestUtil
import dev.shorthouse.remindme.util.getOrAwaitValue
import dev.shorthouse.remindme.utilities.ReminderSort
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ActiveListViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Class under test
    private lateinit var viewModel: ActiveListViewModel

    private lateinit var reminders: MutableList<Reminder>

    @Before
    fun setup() {
        val pastReminder = TestUtil.createReminder(
            id = 1L,
            name = "pastReminder",
            startDateTime = ZonedDateTime.parse("2000-06-15T19:01:00Z"),
        )

        val reminderNow = TestUtil.createReminder(
            id = 2L,
            name = "reminderNow",
            repeatInterval = RepeatInterval(1, ChronoUnit.DAYS),
            startDateTime = ZonedDateTime.now().minusMinutes(1),
        )

        val futureReminder = TestUtil.createReminder(
            id = 3L,
            name = "futureReminder",
            startDateTime = ZonedDateTime.parse("3000-06-15T19:01:00Z"),
            repeatInterval = RepeatInterval(2, ChronoUnit.WEEKS),
        )

        reminders = mutableListOf(pastReminder, reminderNow, futureReminder)

        val dataSource = FakeDataSource(reminders)
        val reminderRepository = ReminderRepository(dataSource)

        viewModel = ActiveListViewModel(reminderRepository, StandardTestDispatcher())
    }

    @Test
    fun `Sort active reminders by earliest date first, sorted by date earliest first`() {
        val sort = MutableLiveData(ReminderSort.EARLIEST_DATE_FIRST)
        val filter = MutableLiveData("")
        val expectedSortedReminders = reminders.subList(0, 2)
            .sortedBy { it.startDateTime }

        val sortedReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()

        assertThat(sortedReminders).isEqualTo(expectedSortedReminders)
    }

    @Test
    fun `Sort active reminders by oldest date first, sorted by date oldest first`() {
        val sort = MutableLiveData(ReminderSort.LATEST_DATE_FIRST)
        val filter = MutableLiveData("")
        val expectedSortedReminders = reminders.subList(0, 2)
            .sortedByDescending { it.startDateTime }

        val sortedReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()

        assertThat(sortedReminders).isEqualTo(expectedSortedReminders)
    }

    @Test
    fun `Sort is null, returns null`() {
        val sort = MutableLiveData<ReminderSort>(null)
        val filter = MutableLiveData("")

        val nullReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()

        assertThat(nullReminders).isNull()
    }

    @Test
    fun `Filter is null, returns null`() {
        val sort = MutableLiveData(ReminderSort.LATEST_DATE_FIRST)
        val filter = MutableLiveData<String>(null)

        val nullReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()

        assertThat(nullReminders).isNull()
    }

    @Test
    fun `Filter on current time, returns only active reminders`() {
        val sort = MutableLiveData(ReminderSort.EARLIEST_DATE_FIRST)
        val filter = MutableLiveData("")

        val filterTimeReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()

        assertThat(filterTimeReminders).isEqualTo(reminders.subList(0, 2))
    }

    @Test
    fun `Filter active reminders by name string, only reminders with that name remain`() {
        val sort = MutableLiveData(ReminderSort.EARLIEST_DATE_FIRST)
        val filter = MutableLiveData("past")
        val expectedFilteredReminders = reminders.subList(0, 2)
            .filter { it.name.contains(filter.getOrAwaitValue(), true) }

        val actualFilteredReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()

        assertThat(actualFilteredReminders).isEqualTo(expectedFilteredReminders)
    }

    @Test
    fun `Filter active reminders with no matching reminders, returns no reminders`() {
        val sort = MutableLiveData(ReminderSort.EARLIEST_DATE_FIRST)
        val filter = MutableLiveData("xxxxx")
        val expectedFilteredReminders = emptyList<Reminder>()

        val actualFilteredReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()

        assertThat(actualFilteredReminders).isEqualTo(expectedFilteredReminders)
    }

    @Test
    fun `Set one-off reminder to done, should archive`() {
        val reminderToArchive = TestUtil.createReminder(
            id = 4L,
            name = "reminderToArchive",
            startDateTime = ZonedDateTime.parse("2000-06-15T19:01:00Z"),
        )
        viewModel.repository.insertReminder(reminderToArchive)

        viewModel.updateDoneReminder(reminderToArchive)

        val updatedDoneReminder = viewModel.repository.getReminder(reminderToArchive.id).asLiveData().getOrAwaitValue()

        assertThat(updatedDoneReminder.isComplete)
    }

    @Test
    fun `Set repeat reminder to done, should not archive`() {
        val reminder = reminders.last()
        viewModel.updateDoneReminder(reminder)

        val updatedDoneReminder = viewModel.repository.getReminder(reminder.id).asLiveData().getOrAwaitValue()

        assertThat(!updatedDoneReminder.isComplete)
    }

    @Test
    fun `Update repeat reminder, should correctly update start date time`() {
        val reminder = reminders[1]
        val expectedUpdatedDateTime = reminder.startDateTime.plusDays(1)

        val updatedDoneReminder = viewModel.getUpdatedDoneReminder(reminder)

        assertThat(updatedDoneReminder.startDateTime).isEqualTo(expectedUpdatedDateTime)
    }

    @Test
    fun `Update current time, updates to correct time`() {
        val nowBefore = ZonedDateTime.now()
        viewModel.updateCurrentTime()
        val nowAfter = ZonedDateTime.now()

        val currentTime = viewModel.currentTime.getOrAwaitValue()

        assertThat(currentTime.isAfter(nowBefore))
        assertThat(currentTime.isBefore(nowAfter))
    }

    @Test
    fun `Get millis until next minute, returns correct value`() {
        val currentSeconds = 32

        val dateTime = LocalDateTime.of(
            2000, 1, 1, 1, 1, currentSeconds, 0
        )

        val millisUntilNextMinuteExpected = (60 - currentSeconds) * 1000

        val millisUntilNextMinute = viewModel.getMillisUntilNextMinute(dateTime)

        assertThat(millisUntilNextMinute).isEqualTo(millisUntilNextMinuteExpected)
    }
}

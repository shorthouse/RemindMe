package dev.shorthouse.remindme.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.getOrAwaitValue
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.RemindersSort
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.ZoneId
import java.time.ZonedDateTime

@RunWith(AndroidJUnit4::class)
class AllListViewModelTest {

    // Class under test
    private lateinit var viewModel: AllListViewModel

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
                2020,
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

        reminders = mutableListOf(reminderOlder, reminderNewer)

        val dataSource = FakeDataSource(reminders)
        val reminderRepository = ReminderRepository(dataSource)
        viewModel = AllListViewModel(reminderRepository)
    }

    @Test
    fun `sort reminders newest first, newest is first`() {
        val sort = MutableLiveData(RemindersSort.EARLIEST_DATE_FIRST)
        val filter = MutableLiveData("")

        val sortedReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()

        assertThat(sortedReminders).isEqualTo(reminders.sortedByDescending { it.startDateTime })
    }

    @Test
    fun `sort reminders oldest first, oldest is first`() {
        val sort = MutableLiveData(RemindersSort.LATEST_DATE_FIRST)
        val filter = MutableLiveData("")

        val sortedReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()

        assertThat(sortedReminders).isEqualTo(reminders.sortedBy { it.startDateTime })
    }

    @Test
    fun `sort is null, returns null`() {
        val sort = MutableLiveData<RemindersSort>(null)
        val filter = MutableLiveData<String>(null)

        val nullReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()

        assertThat(nullReminders).isNull()
    }

    @Test
    fun `filter reminders by name string, only reminders with that name remain`() {
        val sort = MutableLiveData(RemindersSort.EARLIEST_DATE_FIRST)
        val filter = MutableLiveData("reminder2")

        val filteredReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()

        assertThat(filteredReminders).isEqualTo(listOf(reminders.last()))
    }

    @Test
    fun `filter is null, treats filter as blank`() {
        val sort = MutableLiveData(RemindersSort.LATEST_DATE_FIRST)
        val filter = MutableLiveData<String>(null)

        val nullFilterReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()

        assertThat(nullFilterReminders).isEqualTo(reminders.sortedBy { it.startDateTime })
    }

    @Test
    fun `filter is only whitespace, treats filter as blank`() {
        val sort = MutableLiveData(RemindersSort.LATEST_DATE_FIRST)
        val filter = MutableLiveData("      ")

        val whitespaceFilterReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()

        assertThat(whitespaceFilterReminders).isEqualTo(reminders.sortedBy { it.startDateTime })
    }

    @Test
    fun `sort and filter with valid result, returns valid result`() {
        val sort = MutableLiveData(RemindersSort.EARLIEST_DATE_FIRST)
        val filter = MutableLiveData("reminder1")

        val sortFilterReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()

        assertThat(sortFilterReminders).isEqualTo(reminders.subList(0, 1))
    }

    @Test
    fun `sort and filter results in empty list, returns empty list`() {
        val sort = MutableLiveData(RemindersSort.EARLIEST_DATE_FIRST)
        val filter = MutableLiveData("x")

        val sortFilterReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()

        assertThat(sortFilterReminders).isEmpty()
    }
}
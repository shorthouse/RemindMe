package dev.shorthouse.remindme.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.util.TestUtil
import dev.shorthouse.remindme.util.getOrAwaitValue
import dev.shorthouse.remindme.utilities.enums.ReminderSortOrder
import io.mockk.MockKAnnotations
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.ZonedDateTime

@RunWith(AndroidJUnit4::class)
class ListScheduledViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var listScheduledViewModel: ListScheduledViewModel

    private lateinit var reminderRepository: ReminderRepository

    private val scheduledReminderEarlierDate = TestUtil.createReminder(
        id = 1,
        name = "scheduledReminderEarlierDate",
        startDateTime = ZonedDateTime.parse("2020-01-01T08:00:00Z")
    )

    private val scheduledReminderLaterDate = TestUtil.createReminder(
        id = 2,
        name = "scheduledReminderLaterDate",
        startDateTime = ZonedDateTime.parse("2020-02-01T09:00:00Z")
    )

    private val completedReminder = TestUtil.createReminder(
        id = 3,
        name = "completedReminder",
        isCompleted = true
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        val fakeReminderDataSource = FakeDataSource(
            mutableListOf(
                scheduledReminderEarlierDate,
                scheduledReminderLaterDate,
                completedReminder
            )
        )

        reminderRepository = ReminderRepository(fakeReminderDataSource)

        listScheduledViewModel = ListScheduledViewModel(
            repository = reminderRepository
        )
    }

    @Test
    fun `Get completed reminders, returns only completed reminders`() {
        val scheduledReminders =
            listScheduledViewModel.getScheduledReminders(ReminderSortOrder.EARLIEST_DATE_FIRST).getOrAwaitValue()

        assertThat(scheduledReminders).contains(scheduledReminderEarlierDate)
        assertThat(scheduledReminders).contains(scheduledReminderLaterDate)
    }

    @Test
    fun `Get completed reminders earliest date first, returns reminders sorted by date ascending`() {
        val scheduledReminders =
            listScheduledViewModel.getScheduledReminders(ReminderSortOrder.EARLIEST_DATE_FIRST).getOrAwaitValue()

        assertThat(scheduledReminders).isEqualTo(listOf(scheduledReminderEarlierDate, scheduledReminderLaterDate))
    }

    @Test
    fun `Get completed reminders latest date first, returns reminders sorted by date descending`() {
        val scheduledReminders =
            listScheduledViewModel.getScheduledReminders(ReminderSortOrder.LATEST_DATE_FIRST).getOrAwaitValue()

        assertThat(scheduledReminders).isEqualTo(listOf(scheduledReminderLaterDate, scheduledReminderEarlierDate))
    }
}

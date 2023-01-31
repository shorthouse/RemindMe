package dev.shorthouse.remindme.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.util.ReminderTestUtil
import dev.shorthouse.remindme.util.getOrAwaitValue
import dev.shorthouse.remindme.utilities.enums.ReminderSortOrder
import io.mockk.MockKAnnotations
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.ZonedDateTime

@RunWith(AndroidJUnit4::class)
class ListCompletedViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var listCompletedViewModel: ListCompletedViewModel

    private lateinit var reminderRepository: ReminderRepository

    private val completedReminderEarlierDate = ReminderTestUtil().createReminder(
        id = 1,
        name = "completedReminderEarlierDate",
        startDateTime = ZonedDateTime.parse("2020-01-01T08:00:00Z"),
        isCompleted = true
    )

    private val completedReminderLaterDate = ReminderTestUtil().createReminder(
        id = 2,
        name = "completedReminderLaterDate",
        startDateTime = ZonedDateTime.parse("2020-02-01T09:00:00Z"),
        isCompleted = true
    )

    private val uncompletedReminder = ReminderTestUtil().createReminder(
        id = 3,
        name = "reminderToEdit",
        isCompleted = false
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        val fakeReminderDataSource = FakeDataSource(
            mutableListOf(
                completedReminderEarlierDate,
                completedReminderLaterDate,
                uncompletedReminder
            )
        )
        reminderRepository = ReminderRepository(fakeReminderDataSource)

        listCompletedViewModel = ListCompletedViewModel(
            repository = reminderRepository
        )
    }

    @Test
    fun `Get completed reminders, returns only completed reminders`() {
        val completedReminders =
            listCompletedViewModel.getCompletedReminders(ReminderSortOrder.EARLIEST_DATE_FIRST).getOrAwaitValue()

        assertThat(completedReminders).contains(completedReminderEarlierDate)
        assertThat(completedReminders).contains(completedReminderLaterDate)
    }

    @Test
    fun `Get completed reminders earliest date first, returns reminders sorted by date ascending`() {
        val completedReminders =
            listCompletedViewModel.getCompletedReminders(ReminderSortOrder.EARLIEST_DATE_FIRST).getOrAwaitValue()

        assertThat(completedReminders).isEqualTo(listOf(completedReminderEarlierDate, completedReminderLaterDate))
    }

    @Test
    fun `Get completed reminders latest date first, returns reminders sorted by date descending`() {
        val completedReminders =
            listCompletedViewModel.getCompletedReminders(ReminderSortOrder.LATEST_DATE_FIRST).getOrAwaitValue()

        assertThat(completedReminders).isEqualTo(listOf(completedReminderLaterDate, completedReminderEarlierDate))
    }
}

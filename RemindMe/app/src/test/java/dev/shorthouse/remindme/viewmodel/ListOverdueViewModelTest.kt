package dev.shorthouse.remindme.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.model.RepeatInterval
import dev.shorthouse.remindme.util.TestUtil
import dev.shorthouse.remindme.util.getOrAwaitValue
import dev.shorthouse.remindme.utilities.NotificationScheduler
import dev.shorthouse.remindme.utilities.enums.ReminderSortOrder
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ListOverdueViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var listOverdueViewModel: ListOverdueViewModel

    private lateinit var reminderRepository: ReminderRepository

    private lateinit var ioDispatcher: CoroutineDispatcher

    @MockK
    private lateinit var notificationScheduler: NotificationScheduler

    private val overdueReminderEarlierDate = TestUtil.createReminder(
        id = 1,
        name = "overdueReminderEarlierDate",
        startDateTime = ZonedDateTime.parse("2000-01-01T08:00:00Z"),
        isCompleted = false
    )

    private val overdueReminderLaterDate = TestUtil.createReminder(
        id = 2,
        name = "overdueReminderLaterDate",
        startDateTime = ZonedDateTime.parse("2010-01-01T08:00:00Z"),
        isCompleted = false
    )

    private val scheduledReminder = TestUtil.createReminder(
        id = 3,
        name = "scheduledReminder",
        startDateTime = ZonedDateTime.parse("3000-01-01T08:00:00Z"),
        isCompleted = false
    )

    private val completedReminder = TestUtil.createReminder(
        id = 4,
        name = "completedReminder",
        isCompleted = true
    )

    private val oneOffReminderToComplete = TestUtil.createReminder(
        id = 5,
        name = "oneOffReminderToComplete",
        repeatInterval = null,
        isCompleted = false
    )

    private val repeatReminderToComplete = TestUtil.createReminder(
        id = 6,
        name = "repeatReminderToComplete",
        repeatInterval = RepeatInterval(1, ChronoUnit.DAYS),
        startDateTime = ZonedDateTime.now().minusMonths(1).truncatedTo(ChronoUnit.DAYS).withHour(8),
        isCompleted = false
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        val fakeReminderDataSource = FakeDataSource(
            mutableListOf(
                overdueReminderEarlierDate,
                overdueReminderLaterDate,
                scheduledReminder,
                completedReminder,
                oneOffReminderToComplete,
                repeatReminderToComplete
            )
        )

        reminderRepository = ReminderRepository(fakeReminderDataSource)
        ioDispatcher = StandardTestDispatcher()

        listOverdueViewModel = ListOverdueViewModel(
            repository = reminderRepository,
            notificationScheduler = notificationScheduler,
            ioDispatcher = ioDispatcher
        )
    }

    @Test
    fun `Get overdue reminders, returns only overdue reminders`() {
        val overdueReminders =
            listOverdueViewModel.getOverdueReminders(ReminderSortOrder.EARLIEST_DATE_FIRST).getOrAwaitValue()

        assertThat(overdueReminders).contains(overdueReminderEarlierDate)
        assertThat(overdueReminders).contains(overdueReminderLaterDate)
        assertThat(overdueReminders).doesNotContain(scheduledReminder)
        assertThat(overdueReminders).doesNotContain(completedReminder)
    }

    @Test
    fun `Get overdue reminders earliest date first, returns reminders sorted by date ascending`() {
        val overdueReminders =
            listOverdueViewModel.getOverdueReminders(ReminderSortOrder.EARLIEST_DATE_FIRST).getOrAwaitValue()

        assertThat(overdueReminders).isEqualTo(listOf(overdueReminderEarlierDate, overdueReminderLaterDate))
    }

    @Test
    fun `Get scheduled reminders latest date first, returns reminders sorted by date descending`() {
        val overdueReminders =
            listOverdueViewModel.getOverdueReminders(ReminderSortOrder.LATEST_DATE_FIRST).getOrAwaitValue()

        assertThat(overdueReminders).isEqualTo(listOf(overdueReminderLaterDate, overdueReminderEarlierDate))
    }

    @Test
    fun `Update completed one-off reminder, completes reminder`() = runTest(ioDispatcher) {
        val expectedCompletedReminder = oneOffReminderToComplete.copy(isCompleted = true)

        listOverdueViewModel.updateCompletedReminder(oneOffReminderToComplete)
        advanceUntilIdle()

        val completedReminder = reminderRepository.getReminder(5).asLiveData().getOrAwaitValue()
        assertThat(completedReminder).isEqualTo(expectedCompletedReminder)
    }

    @Test
    fun `Update completed repeat reminder, updates to new start time of reminder`() = runTest(ioDispatcher) {
        val expectedCompletedReminder = repeatReminderToComplete.copy(
            startDateTime = ZonedDateTime.now()
                .plusDays(1)
                .truncatedTo(ChronoUnit.DAYS)
                .withHour(8)
        )

        listOverdueViewModel.updateCompletedReminder(repeatReminderToComplete)
        advanceUntilIdle()

        val completedReminder = reminderRepository.getReminder(6).asLiveData().getOrAwaitValue()
        assertThat(completedReminder).isEqualTo(expectedCompletedReminder)
    }
}

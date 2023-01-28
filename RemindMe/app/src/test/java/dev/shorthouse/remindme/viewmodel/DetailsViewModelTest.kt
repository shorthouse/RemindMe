package dev.shorthouse.remindme.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.util.TestUtil
import dev.shorthouse.remindme.util.getOrAwaitValue
import dev.shorthouse.remindme.utilities.NotificationScheduler
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class DetailsViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var detailsViewModel: DetailsViewModel

    private lateinit var reminderRepository: ReminderRepository

    private lateinit var ioDispatcher: CoroutineDispatcher

    @MockK
    private lateinit var notificationScheduler: NotificationScheduler

    private val reminderToGet = TestUtil.createReminder(
        id = 1,
        name = "reminderToGet"
    )

    private val reminderToDelete = TestUtil.createReminder(
        id = 2,
        name = "reminderToDelete"
    )

    private val reminderToComplete = TestUtil.createReminder(
        id = 3,
        name = "reminderToComplete"
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        MockKAnnotations.init(this)

        val fakeReminderDataSource = FakeDataSource(
            mutableListOf(
                reminderToGet,
                reminderToDelete,
                reminderToComplete
            )
        )
        reminderRepository = ReminderRepository(fakeReminderDataSource)
        ioDispatcher = StandardTestDispatcher()

        detailsViewModel = DetailsViewModel(
            repository = reminderRepository,
            notificationScheduler = notificationScheduler,
            ioDispatcher = ioDispatcher
        )
    }

    @Test
    fun `Get reminder details, returns expected reminder`() {
        val reminder = detailsViewModel.getReminderDetails(reminderId = 1).getOrAwaitValue()

        assertThat(reminder).isEqualTo(reminderToGet)
    }

    @Test
    fun `Delete reminder, deletes expected reminder`() = runTest(ioDispatcher) {
        detailsViewModel.deleteReminder(reminderToDelete)
        advanceUntilIdle()

        assertThrows(NoSuchElementException::class.java) { reminderRepository.getReminder(2) }
    }

    @Test
    fun `Complete reminder, completes expected reminder`() = runTest(ioDispatcher) {
        detailsViewModel.completeReminder(reminderToComplete)
        advanceUntilIdle()

        val completedReminder = reminderRepository.getReminder(reminderToComplete.id).asLiveData().getOrAwaitValue()
        assertThat(completedReminder.isCompleted).isTrue()
    }
}

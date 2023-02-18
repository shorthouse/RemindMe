package dev.shorthouse.remindme.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.util.ReminderTestUtil
import dev.shorthouse.remindme.util.getOrAwaitValue
import dev.shorthouse.remindme.util.NotificationScheduler
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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class InputViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var inputViewModel: InputViewModel

    private lateinit var reminderRepository: ReminderRepository

    private lateinit var ioDispatcher: CoroutineDispatcher

    private val reminderToAdd = ReminderTestUtil().createReminder(
        id = 0,
        name = "reminderToAdd"
    )

    private val reminderToEdit = ReminderTestUtil().createReminder(
        id = 1,
        name = "reminderToEdit"
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        MockKAnnotations.init(this)

        val fakeReminderDataSource = FakeDataSource(
            mutableListOf(
                reminderToEdit
            )
        )
        reminderRepository = ReminderRepository(fakeReminderDataSource)
        ioDispatcher = StandardTestDispatcher()

        inputViewModel = InputViewModel(
            reminderRepository = reminderRepository,
            ioDispatcher = ioDispatcher
        )
    }

    @Test
    fun `Is reminder valid with empty name, returns false`() {
        val emptyNameReminder = reminderToAdd.copy(name = "")

        val isReminderValid = inputViewModel.isReminderValid(emptyNameReminder)

        assertThat(isReminderValid).isFalse()
    }

    @Test
    fun `Is reminder valid with blank name, returns false`() {
        val blankNameReminder = reminderToAdd.copy(name = "     ")

        val isReminderValid = inputViewModel.isReminderValid(blankNameReminder)

        assertThat(isReminderValid).isFalse()
    }

    @Test
    fun `Is reminder valid with start date time in past and blank name, returns false`() {
        val startDateTimeInPastReminder = reminderToAdd.copy(
            name = "     ",
            startDateTime = ZonedDateTime.now().minusDays(1)
        )

        val isReminderValid = inputViewModel.isReminderValid(startDateTimeInPastReminder)

        assertThat(isReminderValid).isFalse()
    }

    @Test
    fun `Is reminder valid with name and start date time in future, returns true`() {
        val startDateTimeInPastReminder = reminderToAdd.copy(
            name = "Non blank name",
            startDateTime = ZonedDateTime.now().plusDays(1)
        )

        val isReminderValid = inputViewModel.isReminderValid(startDateTimeInPastReminder)

        assertThat(isReminderValid).isTrue()
    }

    @Test
    fun `Save reminder called with new reminder, adds reminder to database`() = runTest(ioDispatcher) {
        val reminderToAdd = ReminderTestUtil().createReminder(
            id = 0,
            name = "reminderToAdd"
        )

        val numRemindersBefore = reminderRepository.getAllReminders().asLiveData().getOrAwaitValue().size

        inputViewModel.saveReminder(reminderToAdd)
        advanceUntilIdle()

        val numRemindersAfter = reminderRepository.getAllReminders().asLiveData().getOrAwaitValue().size
        assertThat(numRemindersBefore.plus(1)).isEqualTo(numRemindersAfter)
    }

    @Test
    fun `Save reminder called with existing reminder, edits reminder in database`() = runTest(ioDispatcher) {
        val editedReminder = reminderToEdit.copy(name = "Edited Reminder Name")

        val numRemindersBefore = reminderRepository.getAllReminders().asLiveData().getOrAwaitValue().size

        inputViewModel.saveReminder(editedReminder)
        advanceUntilIdle()

        val numRemindersAfter = reminderRepository.getAllReminders().asLiveData().getOrAwaitValue().size
        assertThat(numRemindersBefore).isEqualTo(numRemindersAfter)

        val fetchedEditedReminder = reminderRepository.getReminder(1).asLiveData().getOrAwaitValue()
        assertThat(fetchedEditedReminder).isEqualTo(editedReminder)
    }
}

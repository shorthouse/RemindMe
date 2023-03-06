package dev.shorthouse.remindme.ui.screen.input

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.domain.reminder.AddReminderUseCase
import dev.shorthouse.remindme.domain.reminder.EditReminderUseCase
import dev.shorthouse.remindme.util.ReminderTestUtil
import io.mockk.mockk
import io.mockk.verify
import java.time.ZonedDateTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class InputViewModelTest {
    private val testCoroutineDispatcher = StandardTestDispatcher()

    private val testCoroutineScope = TestScope(testCoroutineDispatcher + Job())

    private val reminderRepository = ReminderRepository(
        FakeDataSource(
            mutableListOf(
                ReminderTestUtil().createReminder(
                    id = 1
                )
            )
        )
    )

    private val addReminderUseCase: AddReminderUseCase = mockk(relaxed = true)

    private val editReminderUseCase: EditReminderUseCase = mockk(relaxed = true)

    private val inputViewModel = InputViewModel(
        reminderRepository = reminderRepository,
        ioDispatcher = testCoroutineDispatcher,
        addReminderUseCase = addReminderUseCase,
        editReminderUseCase = editReminderUseCase
    )

    @Test
    fun `Set reminder state, ui state updates and returns expected value`() {
        testCoroutineScope.runTest {
            val reminderToEditId = 1L

            inputViewModel.setReminderState(reminderToEditId)
            advanceUntilIdle()

            assertThat(inputViewModel.uiState.value.reminderState.id).isEqualTo(reminderToEditId)
        }
    }

    @Test
    fun `Is reminder valid with empty name, returns false`() {
        val emptyNameReminder = ReminderTestUtil().createReminder(
            name = ""
        )

        val isReminderValid = inputViewModel.isReminderValid(emptyNameReminder)

        assertThat(isReminderValid).isFalse()
    }

    @Test
    fun `Is reminder valid with blank name, returns false`() {
        val blankNameReminder = ReminderTestUtil().createReminder(
            name = "     "
        )

        val isReminderValid = inputViewModel.isReminderValid(blankNameReminder)

        assertThat(isReminderValid).isFalse()
    }

    @Test
    fun `Is reminder valid with start date time in past and blank name, returns false`() {
        val startDateTimeInPastReminder = ReminderTestUtil().createReminder(
            name = "     ",
            startDateTime = ZonedDateTime.now().minusDays(1)
        )

        val isReminderValid = inputViewModel.isReminderValid(startDateTimeInPastReminder)

        assertThat(isReminderValid).isFalse()
    }

    @Test
    fun `Is reminder valid with name and start date time in future, returns true`() {
        val validReminder = ReminderTestUtil().createReminder(
            name = "Non blank name",
            startDateTime = ZonedDateTime.now().plusDays(1)
        )

        val isReminderValid = inputViewModel.isReminderValid(validReminder)

        assertThat(isReminderValid).isTrue()
    }

    @Test
    fun `Save reminder called with new reminder, calls add reminder use case`() {
        val reminder = ReminderTestUtil().createReminder(
            id = 0
        )

        inputViewModel.saveReminder(reminder)

        verify { addReminderUseCase(reminder) }
    }

    @Test
    fun `Save reminder called with existing reminder, calls edit reminder use case`() {
        val reminder = ReminderTestUtil().createReminder(
            id = 1
        )

        inputViewModel.saveReminder(reminder)

        verify { editReminderUseCase(reminder) }
    }

    @After
    fun cleanUp() {
        testCoroutineScope.cancel()
    }
}

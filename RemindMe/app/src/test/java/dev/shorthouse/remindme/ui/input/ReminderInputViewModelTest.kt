package dev.shorthouse.remindme.ui.input

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.source.local.ReminderRepository
import dev.shorthouse.remindme.domain.reminder.AddReminderUseCase
import dev.shorthouse.remindme.domain.reminder.EditReminderUseCase
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.util.ReminderTestUtil
import io.mockk.mockk
import io.mockk.verify
import java.time.ZonedDateTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ReminderInputViewModelTest {
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

    private val reminderInputViewModel = ReminderInputViewModel(
        reminderRepository = reminderRepository,
        ioDispatcher = testCoroutineDispatcher,
        addReminderUseCase = addReminderUseCase,
        editReminderUseCase = editReminderUseCase
    )

    @After
    fun cleanup() {
        testCoroutineScope.cancel()
    }

    @Test
    fun `Uninitialised UI state, contains expected values`() {
        val expectedUiState = InputUiState(
            reminder = ReminderState().toReminder(),
            isLoading = false
        )

        val uiState = reminderInputViewModel.uiState.value

        assertThat(uiState).isEqualTo(expectedUiState)
    }

    @Test
    fun `Initialised UI state, contains expected values`() {
        testCoroutineScope.runTest {
            val reminderToEditId = 1L

            val expectedUiState = InputUiState(
                reminder = reminderRepository.getReminder(reminderToEditId).first(),
                isLoading = false
            )

            reminderInputViewModel.setReminder(reminderToEditId)
            advanceUntilIdle()

            val uiState = reminderInputViewModel.uiState.value

            assertThat(uiState).isEqualTo(expectedUiState)
        }
    }

    @Test
    fun `Save reminder called with new reminder, calls add reminder use case`() {
        val reminder = ReminderTestUtil().createReminder(
            id = 0
        )

        reminderInputViewModel.saveReminder(reminder)

        verify { addReminderUseCase(reminder) }
    }

    @Test
    fun `Save reminder called with existing reminder, calls edit reminder use case`() {
        val reminder = ReminderTestUtil().createReminder(
            id = 1
        )

        reminderInputViewModel.saveReminder(reminder)

        verify { editReminderUseCase(reminder) }
    }

    @Test
    fun `Is reminder valid with empty name, returns false`() {
        val emptyNameReminder = ReminderTestUtil().createReminder(
            name = ""
        )

        val isReminderValid = reminderInputViewModel.isReminderValid(emptyNameReminder)

        assertThat(isReminderValid).isFalse()
    }

    @Test
    fun `Is reminder valid with blank name, returns false`() {
        val blankNameReminder = ReminderTestUtil().createReminder(
            name = "     "
        )

        val isReminderValid = reminderInputViewModel.isReminderValid(blankNameReminder)

        assertThat(isReminderValid).isFalse()
    }

    @Test
    fun `Is reminder valid with start date time in past and blank name, returns false`() {
        val startDateTimeInPastReminder = ReminderTestUtil().createReminder(
            name = "     ",
            startDateTime = ZonedDateTime.now().minusDays(1)
        )

        val isReminderValid = reminderInputViewModel.isReminderValid(startDateTimeInPastReminder)

        assertThat(isReminderValid).isFalse()
    }

    @Test
    fun `Is reminder valid with name and start date time in future, returns true`() {
        val validReminder = ReminderTestUtil().createReminder(
            name = "Non blank name",
            startDateTime = ZonedDateTime.now().plusDays(1)
        )

        val isReminderValid = reminderInputViewModel.isReminderValid(validReminder)

        assertThat(isReminderValid).isTrue()
    }
}

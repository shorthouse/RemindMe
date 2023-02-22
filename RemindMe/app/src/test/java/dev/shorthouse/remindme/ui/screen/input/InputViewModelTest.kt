package dev.shorthouse.remindme.ui.screen.input

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.domain.reminder.AddReminderUseCase
import dev.shorthouse.remindme.domain.reminder.EditReminderUseCase
import dev.shorthouse.remindme.util.ReminderTestUtil
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.ZonedDateTime

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class InputViewModelTest {
    private lateinit var inputViewModel: InputViewModel

    private lateinit var ioDispatcher: CoroutineDispatcher

    @RelaxedMockK
    private lateinit var addReminderUseCase: AddReminderUseCase

    @RelaxedMockK
    private lateinit var editReminderUseCase: EditReminderUseCase

    private val reminderToEdit = ReminderTestUtil().createReminder(
        id = 1,
        name = "reminderToEdit"
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        ioDispatcher = StandardTestDispatcher()

        val fakeReminderDataSource = FakeDataSource(
            mutableListOf(
                reminderToEdit
            )
        )

        val reminderRepository = ReminderRepository(fakeReminderDataSource)

        inputViewModel = InputViewModel(
            reminderRepository = reminderRepository,
            ioDispatcher = ioDispatcher,
            addReminderUseCase = addReminderUseCase,
            editReminderUseCase = editReminderUseCase
        )
    }

    @Test
    fun `Set reminder state, ui state updates and returns expected value`() = runTest(ioDispatcher) {
        inputViewModel.setReminderState(reminderToEdit.id)
        advanceUntilIdle()

        assertThat(inputViewModel.uiState.value.reminderState.id).isEqualTo(reminderToEdit.id)
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
        val reminderToAdd = ReminderTestUtil().createReminder(
            id = 0,
            name = "reminderToAdd"
        )

        inputViewModel.saveReminder(reminderToAdd)

        verify { addReminderUseCase(reminderToAdd) }
    }

    @Test
    fun `Save reminder called with existing reminder, calls edit reminder use case`() {
        val editedReminder = reminderToEdit.copy(name = "Edited Reminder Name")

        inputViewModel.saveReminder(editedReminder)

        verify { editReminderUseCase(editedReminder) }
    }
}

package dev.shorthouse.remindme.ui.screen.list

import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.shorthouse.remindme.domain.reminder.CompleteOnetimeReminderUseCase
import dev.shorthouse.remindme.domain.reminder.CompleteRepeatReminderOccurrenceUseCase
import dev.shorthouse.remindme.domain.reminder.CompleteRepeatReminderSeriesUseCase
import dev.shorthouse.remindme.domain.reminder.DeleteReminderUseCase
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.ui.util.enums.ReminderAction
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ListViewModelTest {
    private val completeOnetimeReminderUseCase: CompleteOnetimeReminderUseCase = mockk(relaxed = true)

    private val completeRepeatReminderOccurrenceUseCase: CompleteRepeatReminderOccurrenceUseCase = mockk(relaxed = true)

    private val completeRepeatReminderSeriesUseCase: CompleteRepeatReminderSeriesUseCase = mockk(relaxed = true)

    private val deleteReminderUseCase: DeleteReminderUseCase = mockk(relaxed = true)

    private val listViewModel: ListViewModel = ListViewModel(
        completeOnetimeReminderUseCase,
        completeRepeatReminderOccurrenceUseCase,
        completeRepeatReminderSeriesUseCase,
        deleteReminderUseCase
    )

    @Test
    fun `Process complete onetime reminder action, expected use case is called`() {
        val reminderState = ReminderState()
        val reminderAction = ReminderAction.COMPLETE_ONETIME
        val onEdit = {}

        listViewModel.processReminderAction(
            selectedReminderState = reminderState,
            reminderAction = reminderAction,
            onEdit = onEdit
        )

        verify { completeOnetimeReminderUseCase(any()) }
    }

    @Test
    fun `Process complete repeat occurrence reminder action, expected use case is called`() {
        val reminderState = ReminderState()
        val reminderAction = ReminderAction.COMPLETE_REPEAT_OCCURRENCE
        val onEdit = {}

        listViewModel.processReminderAction(
            selectedReminderState = reminderState,
            reminderAction = reminderAction,
            onEdit = onEdit
        )

        verify { completeRepeatReminderOccurrenceUseCase(any()) }
    }

    @Test
    fun `Process complete repeat series reminder action, expected use case is called`() {
        val reminderState = ReminderState()
        val reminderAction = ReminderAction.COMPLETE_REPEAT_SERIES
        val onEdit = {}

        listViewModel.processReminderAction(
            selectedReminderState = reminderState,
            reminderAction = reminderAction,
            onEdit = onEdit
        )

        verify { completeRepeatReminderSeriesUseCase(any()) }
    }

    @Test
    fun `Process delete reminder action, expected use case is called`() {
        val reminderState = ReminderState()
        val reminderAction = ReminderAction.DELETE
        val onEdit = {}

        listViewModel.processReminderAction(
            selectedReminderState = reminderState,
            reminderAction = reminderAction,
            onEdit = onEdit
        )

        verify { deleteReminderUseCase(any()) }
    }
}

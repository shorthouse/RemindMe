package dev.shorthouse.remindme.viewmodel

import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.model.Reminder
import io.mockk.mockk
import org.junit.Test

class ActiveReminderAdapterViewModelTest {

    @Test
    fun `When mark one off reminder as done should archive`() {
        val mockRepository = mockk<ReminderRepository>()
        val mockReminder = mockk<Reminder>()
        val viewModel = ActiveReminderAdapterViewModel(mockReminder, mockRepository)

        val result = viewModel.updateDoneReminder()
    }
}
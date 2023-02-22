package dev.shorthouse.remindme.ui.screen.list.completed

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.domain.reminder.DeleteCompletedRemindersUseCase
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.util.ReminderTestUtil
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ListCompletedViewModelTest {
    private lateinit var listCompletedViewModel: ListCompletedViewModel

    private lateinit var ioDispatcher: CoroutineDispatcher

    @RelaxedMockK
    lateinit var userPreferencesRepository: UserPreferencesRepository

    @RelaxedMockK
    private lateinit var deleteCompletedRemindersUseCase: DeleteCompletedRemindersUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        ioDispatcher = StandardTestDispatcher()

        val fakeReminderDataSource = FakeDataSource(
            mutableListOf(
                ReminderTestUtil().createReminder(
                    id = 1,
                    name = "Completed reminder",
                    isCompleted = true
                ),
                ReminderTestUtil().createReminder(
                    id = 2,
                    name = "Not completed reminder",
                    isCompleted = false
                )
            )
        )

        val reminderRepository = ReminderRepository(fakeReminderDataSource)

        listCompletedViewModel = ListCompletedViewModel(
            reminderRepository = reminderRepository,
            userPreferencesRepository = userPreferencesRepository,
            ioDispatcher = ioDispatcher,
            deleteCompletedRemindersUseCase = deleteCompletedRemindersUseCase
        )
    }

    @Test
    fun `Default UI state, contains expected values`() {
        val expectedCompletedReminderStates = emptyList<ReminderState>()
        val expectedIsLoading = false

        val uiState = listCompletedViewModel.uiState.value

        assertThat(uiState.completedReminderStates).isEqualTo(expectedCompletedReminderStates)
        assertThat(uiState.isLoading).isEqualTo(expectedIsLoading)
    }

    @Test
    fun `Initialise UI state, contains expected values`() {
        val expectedIsLoading = false

        val uiState = listCompletedViewModel.uiState.value

        assertThat(uiState.completedReminderStates.filter { !it.isCompleted }).isEmpty()
        assertThat(uiState.isLoading).isEqualTo(expectedIsLoading)
    }

    @Test
    fun `Delete completed reminders, calls delete completed reminders use case`() {
        listCompletedViewModel.deleteCompletedReminders()

        verify { deleteCompletedRemindersUseCase() }
    }
}

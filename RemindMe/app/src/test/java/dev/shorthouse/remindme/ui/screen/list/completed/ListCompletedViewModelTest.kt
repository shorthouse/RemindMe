package dev.shorthouse.remindme.ui.screen.list.completed

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesSerializer
import dev.shorthouse.remindme.domain.reminder.DeleteCompletedRemindersUseCase
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.util.ReminderTestUtil
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ListCompletedViewModelTest {
    private val testCoroutineDispatcher = StandardTestDispatcher()

    private val testCoroutineScope = TestScope(testCoroutineDispatcher + Job())

    private val userPreferencesRepository = UserPreferencesRepository(
        DataStoreFactory.create(
            serializer = UserPreferencesSerializer,
            scope = testCoroutineScope,
            produceFile = {
                InstrumentationRegistry.getInstrumentation().targetContext.dataStoreFile("test_datastore")
            }
        )
    )

    private val reminderRepository = ReminderRepository(
        FakeDataSource(
            mutableListOf(
                ReminderTestUtil().createReminder(
                    id = 1,
                    isCompleted = true
                ),
                ReminderTestUtil().createReminder(
                    id = 2,
                    isCompleted = false
                )
            )
        )
    )

    private val deleteCompletedRemindersUseCase: DeleteCompletedRemindersUseCase = mockk(relaxed = true)

    private val listCompletedViewModel = ListCompletedViewModel(
        reminderRepository = reminderRepository,
        userPreferencesRepository = userPreferencesRepository,
        ioDispatcher = testCoroutineDispatcher,
        deleteCompletedRemindersUseCase = deleteCompletedRemindersUseCase
    )

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
        testCoroutineScope.runTest {
            val expectedIsLoading = false

            listCompletedViewModel.initialiseUiState()
            advanceUntilIdle()

            val uiState = listCompletedViewModel.uiState.value

            assertThat(uiState.completedReminderStates).isNotEmpty()
            assertThat(uiState.completedReminderStates.filter { !it.isCompleted }).isEmpty()
            assertThat(uiState.isLoading).isEqualTo(expectedIsLoading)
        }
    }

    @Test
    fun `Delete completed reminders, calls delete completed reminders use case`() {
        listCompletedViewModel.deleteCompletedReminders()

        verify { deleteCompletedRemindersUseCase() }
    }

    @After
    fun cleanUp() {
        testCoroutineScope.cancel()
    }
}

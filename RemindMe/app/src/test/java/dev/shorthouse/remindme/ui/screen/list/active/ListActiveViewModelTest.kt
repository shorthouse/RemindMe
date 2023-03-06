package dev.shorthouse.remindme.ui.screen.list.active

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.protodatastore.ReminderSortOrder
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesSerializer
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.util.ReminderTestUtil
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
class ListActiveViewModelTest {
    private val testCoroutineDispatcher = StandardTestDispatcher()

    private val testCoroutineScope = TestScope(testCoroutineDispatcher + Job())

    private val userPreferencesRepository = UserPreferencesRepository(
        DataStoreFactory.create(
            serializer = UserPreferencesSerializer,
            scope = testCoroutineScope,
            produceFile = {
                InstrumentationRegistry.getInstrumentation().targetContext.dataStoreFile(
                    "test_datastore"
                )
            }
        )
    )

    private val reminderRepository = ReminderRepository(
        FakeDataSource(
            mutableListOf(
                ReminderTestUtil().createReminder(
                    id = 1,
                    name = "Active reminder"
                ),
                ReminderTestUtil().createReminder(
                    id = 2,
                    name = "Not active reminder",
                    isCompleted = true
                )
            )
        )
    )

    private val listActiveViewModel = ListActiveViewModel(
        reminderRepository = reminderRepository,
        userPreferencesRepository = userPreferencesRepository,
        ioDispatcher = testCoroutineDispatcher
    )

    @Test
    fun `Default UI state, contains expected values`() {
        val expectedActiveReminderStates = emptyList<ReminderState>()
        val expectedReminderSortOrder = ReminderSortOrder.BY_EARLIEST_DATE_FIRST
        val expectedIsLoading = false

        val uiState = listActiveViewModel.uiState.value

        assertThat(uiState.activeReminderStates).isEqualTo(expectedActiveReminderStates)
        assertThat(uiState.reminderSortOrder).isEqualTo(expectedReminderSortOrder)
        assertThat(uiState.isLoading).isEqualTo(expectedIsLoading)
    }

    @Test
    fun `Initialise UI state, contains expected values`() {
        testCoroutineScope.runTest {
            listActiveViewModel.initialiseUiState()
            advanceUntilIdle()

            val expectedReminderSortOrder = ReminderSortOrder.BY_EARLIEST_DATE_FIRST
            val expectedIsLoading = false

            val uiState = listActiveViewModel.uiState.value

            assertThat(uiState.activeReminderStates).isNotEmpty()
            assertThat(uiState.activeReminderStates.filter { it.isCompleted }).isEmpty()
            assertThat(uiState.reminderSortOrder).isEqualTo(expectedReminderSortOrder)
            assertThat(uiState.isLoading).isEqualTo(expectedIsLoading)
        }
    }

    @Test
    fun `Update reminder sort order, sets preferences to expected value`() {
        testCoroutineScope.runTest {
            val reminderSortOrder = ReminderSortOrder.BY_LATEST_DATE_FIRST

            listActiveViewModel.updateReminderSortOrder(reminderSortOrder)
            advanceUntilIdle()

            assertThat(userPreferencesRepository.userPreferencesFlow.first().reminderSortOrder)
                .isEqualTo(ReminderSortOrder.BY_LATEST_DATE_FIRST)
        }
    }

    @After
    fun cleanUp() {
        testCoroutineScope.cancel()
    }
}

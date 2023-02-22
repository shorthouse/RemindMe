package dev.shorthouse.remindme.ui.screen.list.active

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.protodatastore.ReminderSortOrder
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.util.ReminderTestUtil
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ListActiveViewModelTest {
    private lateinit var listActiveViewModel: ListActiveViewModel

    private lateinit var ioDispatcher: CoroutineDispatcher

    @RelaxedMockK
    lateinit var userPreferencesRepository: UserPreferencesRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        ioDispatcher = StandardTestDispatcher()

        val fakeReminderDataSource = FakeDataSource(
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

        val reminderRepository = ReminderRepository(fakeReminderDataSource)

        listActiveViewModel = ListActiveViewModel(
            reminderRepository = reminderRepository,
            userPreferencesRepository = userPreferencesRepository,
            ioDispatcher = ioDispatcher
        )
    }

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
        val expectedReminderSortOrder = ReminderSortOrder.BY_EARLIEST_DATE_FIRST
        val expectedIsLoading = false

        val uiState = listActiveViewModel.uiState.value

        assertThat(uiState.activeReminderStates.filter { it.isCompleted }).isEmpty()
        assertThat(uiState.reminderSortOrder).isEqualTo(expectedReminderSortOrder)
        assertThat(uiState.isLoading).isEqualTo(expectedIsLoading)
    }

    @Test
    fun `Update reminder sort order, calls user preferences repository`() = runTest(ioDispatcher) {
        val reminderSortOrder = ReminderSortOrder.BY_LATEST_DATE_FIRST

        listActiveViewModel.updateReminderSortOrder(reminderSortOrder)
        advanceUntilIdle()

        coVerify { userPreferencesRepository.updateReminderSortOrder(reminderSortOrder) }
    }
}

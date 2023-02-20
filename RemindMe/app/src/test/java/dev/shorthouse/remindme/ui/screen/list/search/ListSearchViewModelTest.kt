package dev.shorthouse.remindme.ui.screen.list.search

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.util.ReminderTestUtil
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ListSearchViewModelTest {
    private lateinit var listSearchViewModel: ListSearchViewModel

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
                    name = "Search reminder 1"
                ),
                ReminderTestUtil().createReminder(
                    id = 2,
                    name = "Search reminder 2"
                )
            )
        )

        val reminderRepository = ReminderRepository(fakeReminderDataSource)

        listSearchViewModel = ListSearchViewModel(
            reminderRepository = reminderRepository,
            userPreferencesRepository = userPreferencesRepository,
            ioDispatcher = ioDispatcher,
        )
    }

    @Test
    fun `Default UI state, contains expected values`() {
        val expectedSearchReminderStates = emptyList<ReminderState>()
        val expectedSearchQuery = ""
        val expectedIsLoading = false

        val uiState = listSearchViewModel.uiState.value

        assertThat(uiState.searchReminderStates).isEqualTo(expectedSearchReminderStates)
        assertThat(uiState.searchQuery).isEqualTo(expectedSearchQuery)
        assertThat(uiState.isLoading).isEqualTo(expectedIsLoading)
    }

    @Test
    fun `Initialise UI state, contains expected values`() = runTest(ioDispatcher) {
        val expectedSearchQuery = ""
        val expectedIsLoading = false

        val uiState = listSearchViewModel.uiState.value

        assertThat(uiState.searchReminderStates.all { it.name.contains(expectedSearchQuery) }).isTrue()
        assertThat(uiState.searchQuery).isEqualTo(expectedSearchQuery)
        assertThat(uiState.isLoading).isEqualTo(expectedIsLoading)
    }
}

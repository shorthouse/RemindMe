package dev.shorthouse.remindme.ui.screen.list.search

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesSerializer
import dev.shorthouse.remindme.ui.state.ReminderState
import dev.shorthouse.remindme.util.ReminderTestUtil
import io.mockk.MockKAnnotations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ListSearchViewModelTest {
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
                    name = "Search reminder 1"
                ),
                ReminderTestUtil().createReminder(
                    id = 2,
                    name = "Search reminder 2"
                )
            )
        )
    )

    private val listSearchViewModel = ListSearchViewModel(
        reminderRepository = reminderRepository,
        userPreferencesRepository = userPreferencesRepository,
        ioDispatcher = testCoroutineDispatcher
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)
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
    fun `Initialise UI state, contains expected values`() {
        testCoroutineScope.runTest {
            listSearchViewModel.initialiseUiState()
            advanceUntilIdle()

            val expectedSearchQuery = ""
            val expectedIsLoading = false

            val uiState = listSearchViewModel.uiState.value

            assertThat(uiState.searchReminderStates).isNotEmpty()
            assertThat(
                uiState.searchReminderStates.all {
                    it.name.contains(expectedSearchQuery)
                }
            ).isTrue()
            assertThat(uiState.searchQuery).isEqualTo(expectedSearchQuery)
            assertThat(uiState.isLoading).isEqualTo(expectedIsLoading)
        }
    }

    @Test
    fun `Set UI state search query, contains expected values`() {
        testCoroutineScope.runTest {
            val expectedSearchQuery = "1"
            val expectedIsLoading = false
            val expectedReminderName = "Search reminder 1"
            val expectedReminderSize = 1

            listSearchViewModel.initialiseUiState()
            listSearchViewModel.setSearchQuery("1")
            advanceUntilIdle()

            val uiState = listSearchViewModel.uiState.value

            assertThat(uiState.searchReminderStates.size).isEqualTo(expectedReminderSize)
            assertThat(uiState.searchReminderStates.first().name).isEqualTo(expectedReminderName)
            assertThat(uiState.searchQuery).isEqualTo(expectedSearchQuery)
            assertThat(uiState.isLoading).isEqualTo(expectedIsLoading)
        }
    }

    @After
    fun cleanUp() {
        testCoroutineScope.cancel()
    }
}

package dev.shorthouse.remindme.ui.list

import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.protodatastore.ReminderFilter
import dev.shorthouse.remindme.data.protodatastore.ReminderSort
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesRepository
import dev.shorthouse.remindme.data.protodatastore.UserPreferencesSerializer
import dev.shorthouse.remindme.domain.reminder.CompleteOnetimeReminderUseCase
import dev.shorthouse.remindme.domain.reminder.CompleteRepeatReminderOccurrenceUseCase
import dev.shorthouse.remindme.domain.reminder.CompleteRepeatReminderSeriesUseCase
import dev.shorthouse.remindme.domain.reminder.DeleteReminderUseCase
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.ui.util.enums.ReminderAction
import dev.shorthouse.remindme.util.ReminderTestUtil
import io.mockk.mockk
import io.mockk.verify
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
import java.time.ZonedDateTime

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class ReminderListViewModelTest {
    private val testCoroutineDispatcher = StandardTestDispatcher()

    private val testCoroutineScope = TestScope(testCoroutineDispatcher + Job())

    private val reminderRepository = ReminderRepository(
        FakeDataSource(
            mutableListOf(
                ReminderTestUtil().createReminder(
                    id = 1,
                    name = "Upcoming reminder",
                    startDateTime = ZonedDateTime.parse("2020-01-01T08:30:00Z")
                ),
                ReminderTestUtil().createReminder(
                    id = 2,
                    name = "Completed reminder",
                    isCompleted = true
                )
            )
        )
    )

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

    private val completeOnetimeReminderUseCase: CompleteOnetimeReminderUseCase =
        mockk(relaxed = true)

    private val completeRepeatReminderOccurrenceUseCase: CompleteRepeatReminderOccurrenceUseCase =
        mockk(relaxed = true)

    private val completeRepeatReminderSeriesUseCase: CompleteRepeatReminderSeriesUseCase =
        mockk(relaxed = true)

    private val deleteReminderUseCase: DeleteReminderUseCase =
        mockk(relaxed = true)

    private val reminderListViewModel: ReminderListViewModel = ReminderListViewModel(
        reminderRepository = reminderRepository,
        userPreferencesRepository = userPreferencesRepository,
        ioDispatcher = testCoroutineDispatcher,
        completeOnetimeReminderUseCase,
        completeRepeatReminderOccurrenceUseCase,
        completeRepeatReminderSeriesUseCase,
        deleteReminderUseCase
    )

    @After
    fun cleanup() {
        testCoroutineScope.cancel()
    }

    @Test
    fun `Uninitialised UI state, contains expected values`() {
        val expectedUiState = ListUiState(
            reminders = emptyList(),
            reminderFilter = ReminderFilter.UPCOMING,
            reminderSortOrder = ReminderSort.BY_EARLIEST_DATE_FIRST,
            searchQuery = "",
            isSearchBarShown = false,
            bottomSheetReminder = Reminder(),
            isBottomSheetShown = false,
            isLoading = false
        )

        val uiState = reminderListViewModel.uiState.value

        assertThat(uiState).isEqualTo(expectedUiState)
    }

    @Test
    fun `Initialised UI state, contains expected values`() {
        testCoroutineScope.runTest {
            val expectedUiState = ListUiState(
                reminders = reminderRepository.getUpcomingReminders(ZonedDateTime.now()).first(),
                reminderFilter = ReminderFilter.UPCOMING,
                reminderSortOrder = ReminderSort.BY_EARLIEST_DATE_FIRST,
                searchQuery = "",
                isSearchBarShown = false,
                bottomSheetReminder = Reminder(),
                isBottomSheetShown = false,
                isLoading = false
            )

            reminderListViewModel.initialiseUiState()
            advanceUntilIdle()

            val uiState = reminderListViewModel.uiState.value

            assertThat(uiState).isEqualTo(expectedUiState)
        }
    }

    @Test
    fun `Update reminder sort order, sets user preferences to expected value`() {
        testCoroutineScope.runTest {
            val reminderSortOrder = ReminderSort.BY_LATEST_DATE_FIRST

            reminderListViewModel.updateReminderSortOrder(reminderSortOrder)
            advanceUntilIdle()

            assertThat(userPreferencesRepository.userPreferencesFlow.first().reminderSortOrder)
                .isEqualTo(reminderSortOrder)
        }
    }

    @Test
    fun `Update reminder filter, sets user preferences to expected value`() {
        testCoroutineScope.runTest {
            val reminderFilter = ReminderFilter.OVERDUE

            reminderListViewModel.updateReminderFilter(reminderFilter)
            advanceUntilIdle()

            assertThat(userPreferencesRepository.userPreferencesFlow.first().reminderFilter)
                .isEqualTo(reminderFilter)
        }
    }

    @Test
    fun `Update bottom sheet reminder, sets ui state to expected value`() {
        testCoroutineScope.runTest {
            val bottomSheetReminder = Reminder()

            reminderListViewModel.updateBottomSheetReminder(bottomSheetReminder)
            advanceUntilIdle()

            assertThat(reminderListViewModel.uiState.value.bottomSheetReminder)
                .isEqualTo(bottomSheetReminder)
        }
    }

    @Test
    fun `Update is bottom sheet shown, sets ui state to expected value`() {
        testCoroutineScope.runTest {
            val isBottomSheetShown = true

            reminderListViewModel.updateIsBottomSheetShown(isBottomSheetShown)
            advanceUntilIdle()

            assertThat(reminderListViewModel.uiState.value.isBottomSheetShown)
                .isEqualTo(isBottomSheetShown)
        }
    }

    @Test
    fun `Update search query, sets ui state to expected value`() {
        testCoroutineScope.runTest {
            val searchQuery = "New search query"

            reminderListViewModel.updateSearchQuery(searchQuery)
            advanceUntilIdle()

            assertThat(reminderListViewModel.uiState.value.searchQuery)
                .isEqualTo(searchQuery)
        }
    }

    @Test
    fun `Update is search bar shown, sets ui state to expected value`() {
        testCoroutineScope.runTest {
            val isSearchBarShown = true

            reminderListViewModel.updateIsSearchBarShown(isSearchBarShown)
            advanceUntilIdle()

            assertThat(reminderListViewModel.uiState.value.isSearchBarShown)
                .isEqualTo(isSearchBarShown)
        }
    }

    @Test
    fun `Process complete onetime reminder action, expected use case is called`() {
        val reminderAction = ReminderAction.COMPLETE_ONETIME
        val reminder = Reminder()

        reminderListViewModel.processReminderAction(
            reminderAction = reminderAction,
            reminder = reminder
        )

        verify { completeOnetimeReminderUseCase(reminder = reminder) }
    }

    @Test
    fun `Process complete repeat occurrence reminder action, expected use case is called`() {
        val reminderAction = ReminderAction.COMPLETE_REPEAT_OCCURRENCE
        val reminder = Reminder()

        reminderListViewModel.processReminderAction(
            reminderAction = reminderAction,
            reminder = reminder
        )

        verify { completeRepeatReminderOccurrenceUseCase(reminder = reminder) }
    }

    @Test
    fun `Process complete repeat series reminder action, expected use case is called`() {
        val reminderAction = ReminderAction.COMPLETE_REPEAT_SERIES
        val reminder = Reminder()

        reminderListViewModel.processReminderAction(
            reminderAction = reminderAction,
            reminder = reminder
        )

        verify { completeRepeatReminderSeriesUseCase(reminder = reminder) }
    }

    @Test
    fun `Process delete reminder action, expected use case is called`() {
        val reminderAction = ReminderAction.DELETE
        val reminder = Reminder()

        reminderListViewModel.processReminderAction(
            reminderAction = reminderAction,
            reminder = reminder
        )

        verify { deleteReminderUseCase(reminder = reminder) }
    }
}

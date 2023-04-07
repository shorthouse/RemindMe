package dev.shorthouse.remindme.data.protodatastore

import android.content.Context
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@HiltAndroidTest
@OptIn(ExperimentalCoroutinesApi::class)
class UserPreferencesRepositoryTest {
    private val testContext: Context = InstrumentationRegistry.getInstrumentation().targetContext

    private val testCoroutineDispatcher = StandardTestDispatcher()

    private val testCoroutineScope = TestScope(testCoroutineDispatcher + Job())

    private val testDataStore = DataStoreFactory.create(
        serializer = UserPreferencesSerializer,
        scope = testCoroutineScope,
        produceFile = { testContext.dataStoreFile("test_datastore") }
    )

    private val userPreferencesRepository = UserPreferencesRepository(testDataStore)

    @Before
    fun setup() {
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
        testCoroutineScope.cancel()
    }

    @Test
    fun when_set_sort_to_earliest_date_first_should_update_user_preferences() {
        testCoroutineScope.runTest {
            val expectedReminderSortOrder = ReminderSort.BY_EARLIEST_DATE_FIRST

            userPreferencesRepository.updateReminderSortOrder(
                ReminderSort.BY_EARLIEST_DATE_FIRST
            )

            val reminderSortOrder = userPreferencesRepository
                .userPreferencesFlow
                .first()
                .reminderSortOrder

            assertThat(reminderSortOrder).isEqualTo(expectedReminderSortOrder)
        }
    }

    @Test
    fun when_set_sort_to_latest_date_first_should_update_user_preferences() {
        testCoroutineScope.runTest {
            val expectedReminderSortOrder = ReminderSort.BY_LATEST_DATE_FIRST

            userPreferencesRepository.updateReminderSortOrder(
                ReminderSort.BY_LATEST_DATE_FIRST
            )

            val reminderSortOrder = userPreferencesRepository
                .userPreferencesFlow
                .first()
                .reminderSortOrder

            assertThat(reminderSortOrder).isEqualTo(expectedReminderSortOrder)
        }
    }

    @Test
    fun when_set_filter_to_overdue_should_update_user_preferences() {
        testCoroutineScope.runTest {
            val expectedReminderFilter = ReminderFilter.OVERDUE

            userPreferencesRepository.updateReminderFilter(
                ReminderFilter.OVERDUE
            )

            val reminderFilter = userPreferencesRepository
                .userPreferencesFlow
                .first()
                .reminderFilter

            assertThat(reminderFilter).isEqualTo(expectedReminderFilter)
        }
    }

    @Test
    fun when_set_filter_to_upcoming_should_update_user_preferences() {
        testCoroutineScope.runTest {
            val expectedReminderFilter = ReminderFilter.UPCOMING

            userPreferencesRepository.updateReminderFilter(
                ReminderFilter.UPCOMING
            )

            val reminderFilter = userPreferencesRepository
                .userPreferencesFlow
                .first()
                .reminderFilter

            assertThat(reminderFilter).isEqualTo(expectedReminderFilter)
        }
    }

    @Test
    fun when_set_filter_to_completed_should_update_user_preferences() {
        testCoroutineScope.runTest {
            val expectedReminderFilter = ReminderFilter.COMPLETED

            userPreferencesRepository.updateReminderFilter(
                ReminderFilter.COMPLETED
            )

            val reminderFilter = userPreferencesRepository
                .userPreferencesFlow
                .first()
                .reminderFilter

            assertThat(reminderFilter).isEqualTo(expectedReminderFilter)
        }
    }
}

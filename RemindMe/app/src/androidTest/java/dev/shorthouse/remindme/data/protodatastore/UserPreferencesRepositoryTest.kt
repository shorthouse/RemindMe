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

    @Test
    fun when_write_sort_earliest_date_first_should_write_expected_value() {
        testCoroutineScope.runTest {
            val expectedUserPreferences = UserPreferences(
                reminderSortOrder = ReminderSortOrder.BY_EARLIEST_DATE_FIRST
            )

            userPreferencesRepository.updateReminderSortOrder(
                ReminderSortOrder.BY_EARLIEST_DATE_FIRST
            )

            val userPreferences = userPreferencesRepository.userPreferencesFlow.first()

            assertThat(userPreferences).isEqualTo(expectedUserPreferences)
        }
    }

    @Test
    fun when_write_sort_latest_date_first_should_write_expected_value() {
        testCoroutineScope.runTest {
            val expectedUserPreferences = UserPreferences(
                reminderSortOrder = ReminderSortOrder.BY_LATEST_DATE_FIRST
            )

            userPreferencesRepository.updateReminderSortOrder(
                ReminderSortOrder.BY_LATEST_DATE_FIRST
            )

            val userPreferences = userPreferencesRepository.userPreferencesFlow.first()

            assertThat(userPreferences).isEqualTo(expectedUserPreferences)
        }
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
        testCoroutineScope.cancel()
    }
}

package dev.shorthouse.remindme.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.model.RepeatInterval
import dev.shorthouse.remindme.util.TestUtil
import dev.shorthouse.remindme.util.getOrAwaitValue
import dev.shorthouse.remindme.utilities.NotificationScheduler
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class DetailsViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Class under test
    private lateinit var viewModel: DetailsViewModel

    private lateinit var reminderRepository: ReminderRepository

    @MockK
    private lateinit var notificationScheduler: NotificationScheduler

    private val reminder1 = TestUtil.createReminder(
        id = 1L,
        name = "reminderToDelete",
        startDateTime = ZonedDateTime.parse("2000-06-15T19:01:00Z"),
        repeatInterval = RepeatInterval(1, ChronoUnit.DAYS),
    )

    private val reminder2 = TestUtil.createReminder(
        id = 2L,
        name = "reminder2",
        startDateTime = ZonedDateTime.parse("2000-06-15T19:01:00Z"),
        repeatInterval = RepeatInterval(1, ChronoUnit.WEEKS),
    )

    private val reminders = mutableListOf(reminder1, reminder2)

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        val dataSource = FakeDataSource(reminders)
        reminderRepository = ReminderRepository(dataSource)

        //viewModel = DetailsViewModel(reminderRepository, notificationScheduler, StandardTestDispatcher())
    }

    @Test
    fun `Get valid reminder, returns reminder`() {
        val returnedReminder = viewModel.getReminder(1L).getOrAwaitValue()

        assertThat(returnedReminder).isEqualTo(reminder1)
    }

    @Test
    fun `Get invalid reminder id, throws error`() {
        assertThrows(NoSuchElementException::class.java) {
            viewModel.getReminder(0).getOrAwaitValue()
        }
    }

    @Test
    fun `Get repeat interval id on days, returns days`() {
        val repeatIntervalId = viewModel.getRepeatIntervalStringId(reminder1.repeatInterval!!)
        assertThat(repeatIntervalId).isEqualTo(R.plurals.interval_days)
    }

    @Test
    fun `Get repeat interval id on weeks, returns weeks`() {
        val repeatIntervalId = viewModel.getRepeatIntervalStringId(reminder2.repeatInterval!!)
        assertThat(repeatIntervalId).isEqualTo(R.plurals.interval_weeks)
    }
}

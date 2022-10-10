package dev.shorthouse.remindme.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.data.RepeatInterval
import dev.shorthouse.remindme.getOrAwaitValue
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.utilities.NotificationScheduler
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@RunWith(AndroidJUnit4::class)
class DetailsViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Class under test
    private lateinit var viewModel: DetailsViewModel

    @MockK
    private lateinit var notificationScheduler: NotificationScheduler

    private val reminder1 = Reminder(
        id = 1,
        name = "repeatActiveReminder",
        startDateTime = ZonedDateTime.of(
            2000,
            6,
            15,
            19,
            1,
            0,
            0,
            ZoneId.of("Europe/London")
        ),
        repeatInterval = RepeatInterval(1, ChronoUnit.DAYS),
        notes = "notes",
        isArchived = false,
        isNotificationSent = true
    )

    private val reminder2 = Reminder(
        id = 2,
        name = "repeatActiveReminder",
        startDateTime = ZonedDateTime.of(
            2000,
            6,
            15,
            19,
            1,
            0,
            0,
            ZoneId.of("Europe/London")
        ),
        repeatInterval = RepeatInterval(1, ChronoUnit.WEEKS),
        notes = "notes",
        isArchived = false,
        isNotificationSent = true
    )

    private val reminders = mutableListOf(reminder1, reminder2)

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        val dataSource = FakeDataSource(reminders)
        val reminderRepository = ReminderRepository(dataSource)

        viewModel = DetailsViewModel(reminderRepository, notificationScheduler)
    }

    @Test
    fun `get valid reminder, returns reminder`() {
        val returnedReminder = viewModel.getReminder(1).getOrAwaitValue()

        assertThat(returnedReminder).isEqualTo(reminder1)
    }

    @Test
    fun `get invalid reminder id, throws error`() {
        assertThrows(NoSuchElementException::class.java) {
            viewModel.getReminder(0).getOrAwaitValue()
        }
    }

    @Test
    fun `get repeat interval id on days, returns days`() {
        val repeatIntervalId = viewModel.getRepeatIntervalStringId(reminder1.repeatInterval!!)
        assertThat(repeatIntervalId).isEqualTo(R.plurals.interval_days)
    }

    @Test
    fun `get repeat interval id on weeks, returns weeks`() {
        val repeatIntervalId = viewModel.getRepeatIntervalStringId(reminder2.repeatInterval!!)
        assertThat(repeatIntervalId).isEqualTo(R.plurals.interval_weeks)
    }
}
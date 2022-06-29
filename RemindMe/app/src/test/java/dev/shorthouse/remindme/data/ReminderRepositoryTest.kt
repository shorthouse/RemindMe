package dev.shorthouse.remindme.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.getOrAwaitValue
import dev.shorthouse.remindme.model.Reminder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ReminderRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

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
        name = "archivedRepeatReminder",
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
        isArchived = true,
        isNotificationSent = true
    )

    private val reminder3 = Reminder(
        id = 3,
        name = "oneOffNotActiveReminder",
        startDateTime = ZonedDateTime.of(
            3000,
            6,
            15,
            19,
            1,
            0,
            0,
            ZoneId.of("Europe/London")
        ),
        repeatInterval = null,
        notes = null,
        isArchived = false,
        isNotificationSent = false
    )

    private val localReminders = listOf(reminder1, reminder2).sortedBy { it.id }

    private lateinit var reminderLocalDataSource: FakeDataSource

    // Class under test
    private lateinit var reminderRepository: ReminderRepository


    @Before
    fun createRepository() {
        reminderLocalDataSource = FakeDataSource(localReminders.toMutableList())
        reminderRepository = ReminderRepository(reminderLocalDataSource)
    }

    @Test
    fun `Get reminders returns all reminders`() {
        val reminders = reminderRepository.getReminders().asLiveData().getOrAwaitValue()

        assertThat(reminders).isEqualTo(localReminders)
    }

    @Test
    fun `Get reminder returns correct reminder`() {
        val reminder = reminderRepository.getReminder(1).asLiveData().getOrAwaitValue()

        assertThat(reminder).isEqualTo(localReminders.first())
    }

    @Test
    fun `Get non archived reminders returns non archived reminders`() {
        val nonArchivedReminders = reminderRepository
            .getNonArchivedReminders()
            .asLiveData()
            .getOrAwaitValue()

        assertThat(nonArchivedReminders.forEach { it.isArchived.not() })
        assertThat(nonArchivedReminders).contains(reminder1)
    }

    @Test
    fun `Get active non archived reminders returns active non archived reminders`() {
        val activeNonArchivedReminders = reminderRepository
            .getActiveNonArchivedReminders(ZonedDateTime.now())
            .asLiveData()
            .getOrAwaitValue()

        assertThat(activeNonArchivedReminders.forEach {
            it.isArchived.not() && !it.startDateTime.isBefore(
                ZonedDateTime.now()
            )
        })
        assertThat(activeNonArchivedReminders).contains(reminder1)
        assertThat(activeNonArchivedReminders.first()).isEqualTo(localReminders.first())
    }

    @Test
    fun `Insert reminder inserts specified reminder`() {
        val insertedReminderId = reminderRepository.insertReminder(reminder3)
        val allReminders = reminderRepository.getReminders().asLiveData().getOrAwaitValue()

        assertThat(insertedReminderId).isEqualTo(reminder3.id)
        assertThat(allReminders).contains(reminder3)
        assertThat(allReminders).hasSize(3)
    }

    @Test
    fun `Update reminder updates specified reminder`() {
        val updatedReminder = Reminder(
            id = reminder1.id,
            name = "updatedReminder",
            startDateTime = ZonedDateTime.of(
                2700,
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
            isNotificationSent = false
        )
        reminderRepository.updateReminder(updatedReminder)
        val allReminders = reminderRepository.getReminders().asLiveData().getOrAwaitValue()

        assertThat(allReminders).contains(updatedReminder)
        assertThat(allReminders.first().name).isEqualTo("updatedReminder")
    }

    @Test
    fun `Delete reminder deletes specified reminder`() {
        reminderRepository.deleteReminder(reminder1)
        val allReminders = reminderRepository.getReminders().asLiveData().getOrAwaitValue()

        assertThat(allReminders).doesNotContain(reminder1)
        assertThat(allReminders).hasSize(localReminders.size.dec())
    }
}
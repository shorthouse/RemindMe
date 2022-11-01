package dev.shorthouse.remindme.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.model.RepeatInterval
import dev.shorthouse.remindme.util.TestUtil
import dev.shorthouse.remindme.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ReminderRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val repeatActiveReminder = TestUtil.createReminder(
        id = 1L,
        name = "repeatActiveReminder",
        startDateTime = ZonedDateTime.parse("2000-06-15T19:01:00Z"),
        repeatInterval = RepeatInterval(1, ChronoUnit.DAYS),
    )

    private val completedRepeatReminder = TestUtil.createReminder(
        id = 2L,
        name = "completedRepeatReminder",
        startDateTime = ZonedDateTime.parse("2000-06-15T19:01:00Z"),
        repeatInterval = RepeatInterval(1, ChronoUnit.DAYS),
        isComplete = true,
    )

    private val oneOffNotActiveReminder = TestUtil.createReminder(
        id = 3L,
        name = "oneOffNotActiveReminder",
        startDateTime = ZonedDateTime.parse("3000-06-15T19:01:00Z"),
    )

    private val reminderToComplete = TestUtil.createReminder(
        id = 4L,
        name = "reminderToComplete",
        startDateTime = ZonedDateTime.parse("2000-06-15T19:01:00Z"),
        isComplete = false
    )

    private val localReminders =
        listOf(repeatActiveReminder, completedRepeatReminder, reminderToComplete).sortedBy { it.id }

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
    fun `Get not completed reminders returns not completed reminders`() {
        val notCompletedReminders = reminderRepository
            .getNotCompletedReminders()
            .asLiveData()
            .getOrAwaitValue()

        assertThat(notCompletedReminders.forEach { it.isComplete.not() })
        assertThat(notCompletedReminders).contains(repeatActiveReminder)
    }

    @Test
    fun `Complete reminder completes specified reminder`() {
        val expectedCompletedReminder = TestUtil.createReminder(
            reminderToComplete.id,
            reminderToComplete.name,
            reminderToComplete.startDateTime,
            isComplete = true
        )

        reminderRepository.completeReminder(4L)
        val completedReminder = reminderRepository.getReminder(4L).asLiveData().getOrAwaitValue()

        assertThat(completedReminder).isEqualTo(expectedCompletedReminder)
    }

    @Test
    fun `Insert reminder inserts specified reminder`() {
        val insertedReminderId = reminderRepository.insertReminder(oneOffNotActiveReminder)
        val allReminders = reminderRepository.getReminders().asLiveData().getOrAwaitValue()

        assertThat(insertedReminderId).isEqualTo(oneOffNotActiveReminder.id)
        assertThat(allReminders).contains(oneOffNotActiveReminder)
        assertThat(allReminders).hasSize(4)
    }

    @Test
    fun `Update reminder updates specified reminder`() {
        val updatedReminder = TestUtil.createReminder(
            id = repeatActiveReminder.id,
            name = "updatedReminder",
            startDateTime = ZonedDateTime.parse("2700-06-15T19:01:00Z"),
            repeatInterval = RepeatInterval(1, ChronoUnit.WEEKS),
            notes = "updatedNotes",
        )

        reminderRepository.updateReminder(updatedReminder)
        val allReminders = reminderRepository.getReminders().asLiveData().getOrAwaitValue()

        assertThat(allReminders).contains(updatedReminder)
        assertThat(allReminders.first().name).isEqualTo("updatedReminder")
    }

    @Test
    fun `Delete reminder deletes specified reminder`() {
        reminderRepository.deleteReminder(repeatActiveReminder)
        val allReminders = reminderRepository.getReminders().asLiveData().getOrAwaitValue()

        assertThat(allReminders).doesNotContain(repeatActiveReminder)
        assertThat(allReminders).hasSize(localReminders.size.dec())
    }
}

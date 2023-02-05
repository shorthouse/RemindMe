package dev.shorthouse.remindme.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.util.ReminderTestUtil
import dev.shorthouse.remindme.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.ZonedDateTime

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ReminderRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var reminderRepository: ReminderRepository

    private fun setRepositoryReminders(reminders: List<Reminder>) {
        reminderRepository = ReminderRepository(FakeDataSource(reminders.toMutableList()))
    }

    @Test
    fun `Get reminders, returns all reminders`() {
        val reminderOne = ReminderTestUtil().createReminder(
            id = 1,
            name = "reminderOne",
        )

        val reminderTwo = ReminderTestUtil().createReminder(
            id = 2,
            name = "reminderTwo",
        )

        val repositoryReminders = listOf(reminderOne, reminderTwo)
        setRepositoryReminders(repositoryReminders)

        val reminders = reminderRepository.getAllReminders().asLiveData().getOrAwaitValue()

        assertThat(reminders).isEqualTo(repositoryReminders)
    }

    @Test
    fun `Get reminder, returns correct reminder`() {
        val reminderToGet = ReminderTestUtil().createReminder(
            id = 1,
            name = "reminderToGet",
        )

        val repositoryReminders = listOf(reminderToGet)
        setRepositoryReminders(repositoryReminders)

        val reminder = reminderRepository.getReminder(reminderToGet.id).asLiveData().getOrAwaitValue()

        assertThat(reminder).isEqualTo(reminderToGet)
    }

    @Test
    fun `Get overdue reminders, returns only overdue reminders`() {
        val overdueReminder = ReminderTestUtil().createReminder(
            id = 1,
            name = "overdueReminder",
            startDateTime = ZonedDateTime.parse("2000-01-01T00:00:00Z"),
        )

        val notOverdueReminder = ReminderTestUtil().createReminder(
            id = 2,
            name = "notOverdueReminder",
            startDateTime = ZonedDateTime.parse("3000-01-01T00:00:00Z"),
        )

        val expectedOverdueReminders = listOf(overdueReminder)

        val repositoryReminders = listOf(overdueReminder, notOverdueReminder)
        setRepositoryReminders(repositoryReminders)

        val overdueReminders = reminderRepository.getOverdueReminders().asLiveData().getOrAwaitValue()

        assertThat(overdueReminders).isEqualTo(expectedOverdueReminders)
    }

    @Test
    fun `Get scheduled reminders, returns only scheduled reminders`() {
        val overdueReminder = ReminderTestUtil().createReminder(
            id = 1,
            name = "overdueReminder",
            startDateTime = ZonedDateTime.parse("3000-01-01T00:00:00Z"),
        )

        val scheduledReminder = ReminderTestUtil().createReminder(
            id = 2,
            name = "scheduledReminder",
            startDateTime = ZonedDateTime.parse("2000-01-01T00:00:00Z"),
        )

        val completedReminder = ReminderTestUtil().createReminder(
            id = 3,
            name = "completedReminder",
            isCompleted = true
        )

        val expectedScheduledReminders = listOf(overdueReminder, scheduledReminder)

        val repositoryReminders = listOf(overdueReminder, scheduledReminder, completedReminder)
        setRepositoryReminders(repositoryReminders)

        val scheduledReminders = reminderRepository.getReminders().asLiveData().getOrAwaitValue()

        assertThat(scheduledReminders).isEqualTo(expectedScheduledReminders)
    }

    @Test
    fun `Get completed reminders, returns only completed reminders`() {
        val completedReminder = ReminderTestUtil().createReminder(
            id = 1,
            name = "completedReminder",
            isCompleted = true
        )

        val uncompletedReminder = ReminderTestUtil().createReminder(
            id = 2,
            name = "uncompletedReminder",
            isCompleted = false
        )


        val expectedCompletedReminders = listOf(completedReminder)

        val repositoryReminders = listOf(completedReminder, uncompletedReminder)
        setRepositoryReminders(repositoryReminders)

        val completedReminders = reminderRepository.getCompletedReminders().asLiveData().getOrAwaitValue()

        assertThat(completedReminders).isEqualTo(expectedCompletedReminders)
    }

    @Test
    fun `Insert reminder, inserts specified reminder`() {
        val reminderToInsert = ReminderTestUtil().createReminder(
            id = 1,
            name = "reminderToInsert",
        )

        setRepositoryReminders(emptyList())

        val insertedReminderId = reminderRepository.insertReminder(reminderToInsert)

        val insertedReminder = reminderRepository.getReminder(insertedReminderId).asLiveData().getOrAwaitValue()

        assertThat(insertedReminder).isEqualTo(reminderToInsert)
    }

    @Test
    fun `Update reminder, updates specified reminder`() {
        val reminderToUpdate = ReminderTestUtil().createReminder(
            id = 1,
            name = "reminderToUpdate"
        )

        val repositoryReminders = listOf(reminderToUpdate)
        setRepositoryReminders(repositoryReminders)

        val updatedReminder = reminderToUpdate.copy(
            name = "updatedReminder"
        )

        reminderRepository.updateReminder(updatedReminder)

        val updatedReminderFromRepository =
            reminderRepository.getReminder(updatedReminder.id).asLiveData().getOrAwaitValue()

        assertThat(updatedReminderFromRepository).isEqualTo(updatedReminder)
    }

    @Test
    fun `Delete reminder, deletes specified reminder`() {
        val reminderToDelete = ReminderTestUtil().createReminder(
            id = 1,
            name = "reminderToDelete"
        )

        val repositoryReminders = listOf(reminderToDelete)
        setRepositoryReminders(repositoryReminders)

        reminderRepository.deleteReminder(reminderToDelete)

        val reminders = reminderRepository.getAllReminders().asLiveData().getOrAwaitValue()

        assertThat(reminders).doesNotContain(reminderToDelete)
    }

    @Test
    fun `Complete reminder, completes specified reminder`() {
        val reminderToComplete = ReminderTestUtil().createReminder(
            id = 7,
            name = "reminderToComplete",
            isCompleted = false
        )

        val repositoryReminders = listOf(reminderToComplete)
        setRepositoryReminders(repositoryReminders)

        val expectedCompletedReminder = reminderToComplete.copy(
            isCompleted = true
        )

        reminderRepository.completeReminder(reminderToComplete.id)

        val completedReminder = reminderRepository.getReminder(reminderToComplete.id).asLiveData().getOrAwaitValue()

        assertThat(completedReminder).isEqualTo(expectedCompletedReminder)
    }
}

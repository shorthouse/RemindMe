package dev.shorthouse.remindme.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.model.Reminder
import dev.shorthouse.remindme.util.ReminderTestUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import java.time.ZonedDateTime

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ReminderRepositoryTest {
    private lateinit var reminderRepository: ReminderRepository

    private val testCoroutineScope = TestScope(StandardTestDispatcher() + Job())

    private fun setRepositoryReminders(reminders: List<Reminder>) {
        reminderRepository = ReminderRepository(FakeDataSource(reminders.toMutableList()))
    }

    @Test
    fun `Get reminders, returns all reminders`() {
        testCoroutineScope.runTest {
            val reminderOne = ReminderTestUtil().createReminder(
                id = 1,
                name = "reminderOne"
            )

            val reminderTwo = ReminderTestUtil().createReminder(
                id = 2,
                name = "reminderTwo"
            )

            val repositoryReminders = listOf(reminderOne, reminderTwo)
            setRepositoryReminders(repositoryReminders)

            val reminders = reminderRepository.getReminders().first()

            assertThat(reminders).isEqualTo(repositoryReminders)
        }
    }

    @Test
    fun `Get reminders one shot, returns all reminders`() {
        testCoroutineScope.runTest {
            val reminderOne = ReminderTestUtil().createReminder(
                id = 1,
                name = "reminderOne"
            )

            val reminderTwo = ReminderTestUtil().createReminder(
                id = 2,
                name = "reminderTwo"
            )

            val repositoryReminders = listOf(reminderOne, reminderTwo)
            setRepositoryReminders(repositoryReminders)

            val reminders = reminderRepository.getRemindersOneShot()

            assertThat(reminders).isEqualTo(repositoryReminders)
        }
    }

    @Test
    fun `Get reminder, returns expected reminder`() {
        testCoroutineScope.runTest {
            val reminderToGet = ReminderTestUtil().createReminder(
                id = 1,
                name = "reminderToGet"
            )

            val repositoryReminders = listOf(reminderToGet)
            setRepositoryReminders(repositoryReminders)

            val reminder = reminderRepository.getReminder(reminderToGet.id).first()

            assertThat(reminder).isEqualTo(reminderToGet)
        }
    }

    @Test
    fun `Get reminder one shot, returns expected reminder`() {
        testCoroutineScope.runTest {
            val reminderToGet = ReminderTestUtil().createReminder(
                id = 1,
                name = "reminderToGet"
            )

            val repositoryReminders = listOf(reminderToGet)
            setRepositoryReminders(repositoryReminders)

            val reminder = reminderRepository.getReminderOneShot(reminderToGet.id)

            assertThat(reminder).isEqualTo(reminderToGet)
        }
    }

    @Test
    fun `Get overdue reminders, returns only overdue reminders`() {
        testCoroutineScope.runTest {
            val overdueReminder = ReminderTestUtil().createReminder(
                id = 1,
                name = "overdueReminder",
                startDateTime = ZonedDateTime.parse("2000-01-01T00:00:00Z")
            )

            val upcomingReminder = ReminderTestUtil().createReminder(
                id = 2,
                name = "upcomingReminder",
                startDateTime = ZonedDateTime.parse("3000-01-01T00:00:00Z")
            )

            val completedReminder = ReminderTestUtil().createReminder(
                id = 3,
                name = "completedReminder",
                isCompleted = true
            )

            val expectedOverdueReminders = listOf(overdueReminder)

            val repositoryReminders = listOf(overdueReminder, upcomingReminder, completedReminder)
            setRepositoryReminders(repositoryReminders)

            val overdueReminders = reminderRepository
                .getOverdueReminders(ZonedDateTime.now())
                .first()

            assertThat(overdueReminders).isEqualTo(expectedOverdueReminders)
        }
    }

    @Test
    fun `Get upcoming reminders, returns only upcoming reminders`() {
        testCoroutineScope.runTest {
            val overdueReminder = ReminderTestUtil().createReminder(
                id = 1,
                name = "overdueReminder",
                startDateTime = ZonedDateTime.parse("2000-01-01T00:00:00Z")
            )

            val upcomingReminder = ReminderTestUtil().createReminder(
                id = 2,
                name = "upcomingReminder",
                startDateTime = ZonedDateTime.parse("3000-01-01T00:00:00Z")
            )

            val completedReminder = ReminderTestUtil().createReminder(
                id = 3,
                name = "completedReminder",
                isCompleted = true
            )

            val expectedUpcomingReminders = listOf(upcomingReminder)

            val repositoryReminders = listOf(overdueReminder, upcomingReminder, completedReminder)
            setRepositoryReminders(repositoryReminders)

            val upcomingReminders = reminderRepository
                .getUpcomingReminders(ZonedDateTime.now())
                .first()

            assertThat(upcomingReminders).isEqualTo(expectedUpcomingReminders)
        }
    }

    @Test
    fun `Get completed reminders, returns only completed reminders`() {
        testCoroutineScope.runTest {
            val overdueReminder = ReminderTestUtil().createReminder(
                id = 1,
                name = "overdueReminder",
                startDateTime = ZonedDateTime.parse("2000-01-01T00:00:00Z")
            )

            val upcomingReminder = ReminderTestUtil().createReminder(
                id = 2,
                name = "upcomingReminder",
                startDateTime = ZonedDateTime.parse("3000-01-01T00:00:00Z")
            )

            val completedReminder = ReminderTestUtil().createReminder(
                id = 3,
                name = "completedReminder",
                isCompleted = true
            )

            val expectedCompletedReminders = listOf(completedReminder)

            val repositoryReminders = listOf(overdueReminder, upcomingReminder, completedReminder)
            setRepositoryReminders(repositoryReminders)

            val completedReminders = reminderRepository
                .getCompletedReminders()
                .first()

            assertThat(completedReminders).isEqualTo(expectedCompletedReminders)
        }
    }

    @Test
    fun `Insert reminder, inserts specified reminder`() {
        testCoroutineScope.runTest {
            val reminderToInsert = ReminderTestUtil().createReminder(
                id = 1,
                name = "reminderToInsert"
            )

            setRepositoryReminders(emptyList())

            val insertedReminderId = reminderRepository.insertReminder(reminderToInsert)

            val insertedReminder = reminderRepository.getReminder(insertedReminderId).first()

            assertThat(insertedReminder).isEqualTo(reminderToInsert)
        }
    }

    @Test
    fun `Update reminder, updates specified reminder`() {
        testCoroutineScope.runTest {
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
                reminderRepository.getReminder(updatedReminder.id).first()

            assertThat(updatedReminderFromRepository).isEqualTo(updatedReminder)
        }
    }

    @Test
    fun `Delete reminder, deletes specified reminder`() {
        testCoroutineScope.runTest {
            val reminderToDelete = ReminderTestUtil().createReminder(
                id = 1,
                name = "reminderToDelete"
            )

            val repositoryReminders = listOf(reminderToDelete)
            setRepositoryReminders(repositoryReminders)

            reminderRepository.deleteReminder(reminderToDelete)

            val reminders = reminderRepository.getReminders().first()

            assertThat(reminders).doesNotContain(reminderToDelete)
        }
    }

    @Test
    fun `Complete reminder, completes specified reminder`() {
        testCoroutineScope.runTest {
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

            val completedReminder = reminderRepository.getReminder(reminderToComplete.id).first()

            assertThat(completedReminder).isEqualTo(expectedCompletedReminder)
        }
    }
}

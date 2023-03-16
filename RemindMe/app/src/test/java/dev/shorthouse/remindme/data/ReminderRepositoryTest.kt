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

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ReminderRepositoryTest {
    private lateinit var reminderRepository: ReminderRepository

    private val testCoroutineScope = TestScope(StandardTestDispatcher() + Job())

    private fun setRepositoryReminders(reminders: List<Reminder>) {
        reminderRepository = ReminderRepository(FakeDataSource(reminders.toMutableList()))
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

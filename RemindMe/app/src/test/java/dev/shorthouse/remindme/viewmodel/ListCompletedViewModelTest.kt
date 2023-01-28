package dev.shorthouse.remindme.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.util.TestUtil
import io.mockk.MockKAnnotations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ListCompletedViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var listCompletedViewModel: ListCompletedViewModel

    private lateinit var reminderRepository: ReminderRepository

    private val completedReminderEarlierDate = TestUtil.createReminder(
        id = 0,
        name = "completedReminderEarlierDate",
        isCompleted = true
    )

    private val completedReminderLaterDate = TestUtil.createReminder(
        id = 1,
        name = "completedReminderLaterDate",
        isCompleted = true
    )

    private val uncompletedReminder = TestUtil.createReminder(
        id = 1,
        name = "reminderToEdit",
        isCompleted = false
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        val fakeReminderDataSource = FakeDataSource(
            mutableListOf(
                completedReminderEarlierDate,
                completedReminderLaterDate,
                uncompletedReminder
            )
        )
        reminderRepository = ReminderRepository(fakeReminderDataSource)

        listCompletedViewModel = ListCompletedViewModel(
            repository = reminderRepository
        )
    }

    @Test
    fun `Get completed reminders, returns only completed reminders`() {
//        val emptyNameReminder = reminderToAdd.copy(name = "")
//
//        val isReminderValid = inputViewModel.isReminderValid(emptyNameReminder)
//
//        Truth.assertThat(isReminderValid).isFalse()
    }
}

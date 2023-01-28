package dev.shorthouse.remindme.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dev.shorthouse.remindme.data.FakeDataSource
import dev.shorthouse.remindme.data.ReminderRepository
import dev.shorthouse.remindme.util.TestUtil
import dev.shorthouse.remindme.util.getOrAwaitValue
import io.mockk.MockKAnnotations
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var editViewModel: EditViewModel

    private lateinit var reminderRepository: ReminderRepository

    private val reminderToEdit = TestUtil.createReminder(
        id = 1,
        name = "reminderToEdit"
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        val fakeReminderDataSource = FakeDataSource(
            mutableListOf(
                reminderToEdit
            )
        )
        reminderRepository = ReminderRepository(fakeReminderDataSource)

        editViewModel = EditViewModel(
            repository = reminderRepository
        )
    }

    @Test
    fun `Get reminder details, returns expected reminder`() {
        val reminder = editViewModel.getReminder(reminderId = 1).getOrAwaitValue()

        assertThat(reminder).isEqualTo(reminderToEdit)
    }
}

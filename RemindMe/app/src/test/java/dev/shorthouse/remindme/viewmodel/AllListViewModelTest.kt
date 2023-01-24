//package dev.shorthouse.remindme.viewmodel
//
//import androidx.lifecycle.MutableLiveData
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.google.common.truth.Truth.assertThat
//import dev.shorthouse.remindme.data.FakeDataSource
//import dev.shorthouse.remindme.data.ReminderRepository
//import dev.shorthouse.remindme.model.Reminder
//import dev.shorthouse.remindme.model.RepeatInterval
//import dev.shorthouse.remindme.util.TestUtil
//import dev.shorthouse.remindme.util.getOrAwaitValue
//import dev.shorthouse.remindme.utilities.enums.ReminderSortOrder
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import java.time.ZonedDateTime
//import java.time.temporal.ChronoUnit
//
//@RunWith(AndroidJUnit4::class)
//class AllListViewModelTest {
//
//    // Class under test
//    private lateinit var viewModel: AllListViewModelOld
//
//    private lateinit var reminders: MutableList<Reminder>
//
//    @Before
//    fun setup() {
//        val pastReminder = TestUtil.createReminder(
//            id = 1L,
//            name = "pastReminder",
//            startDateTime = ZonedDateTime.parse("2000-06-15T19:01:00Z"),
//        )
//
//        val futureReminder = TestUtil.createReminder(
//            id = 3L,
//            name = "futureReminder",
//            startDateTime = ZonedDateTime.parse("3000-06-15T19:01:00Z"),
//            repeatInterval = RepeatInterval(2, ChronoUnit.WEEKS),
//        )
//
//        reminders = mutableListOf(pastReminder, futureReminder)
//
//        val dataSource = FakeDataSource(reminders)
//        val reminderRepository = ReminderRepository(dataSource)
//
//        viewModel = AllListViewModelOld(reminderRepository)
//    }
//
//    @Test
//    fun `Sort all reminders by earliest date first, sorted by date earliest first`() {
//        val sort = MutableLiveData(ReminderSortOrder.EARLIEST_DATE_FIRST)
//        val filter = MutableLiveData("")
//        val expectedSortedReminders = reminders.sortedBy { it.startDateTime }
//
//        val sortedReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()
//
//        assertThat(sortedReminders).isEqualTo(expectedSortedReminders)
//    }
//
//    @Test
//    fun `Sort all reminders by oldest date first, sorted by date oldest first`() {
//        val sort = MutableLiveData(ReminderSortOrder.LATEST_DATE_FIRST)
//        val filter = MutableLiveData("")
//        val expectedSortedReminders = reminders.sortedByDescending { it.startDateTime }
//
//        val sortedReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()
//
//        assertThat(sortedReminders).isEqualTo(expectedSortedReminders)
//    }
//
//    @Test
//    fun `Sort is null, returns null`() {
//        val sort = MutableLiveData<ReminderSortOrder>(null)
//        val filter = MutableLiveData<String>(null)
//
//        val nullReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()
//
//        assertThat(nullReminders).isNull()
//    }
//
//    @Test
//    fun `Filter all reminders by name string, only reminders with that name remain`() {
//        val sort = MutableLiveData(ReminderSortOrder.EARLIEST_DATE_FIRST)
//        val filter = MutableLiveData("futureReminder")
//        val expectedFilteredReminders = listOf(reminders.last())
//
//        val filteredReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()
//
//        assertThat(filteredReminders).isEqualTo(expectedFilteredReminders)
//    }
//
//    @Test
//    fun `Filter all reminders with no matching reminders, returns no reminders`() {
//        val sort = MutableLiveData(ReminderSortOrder.EARLIEST_DATE_FIRST)
//        val filter = MutableLiveData("xxxxx")
//        val expectedFilteredReminders = emptyList<Reminder>()
//
//        val actualFilteredReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()
//
//        assertThat(actualFilteredReminders).isEqualTo(expectedFilteredReminders)
//    }
//
//    @Test
//    fun `Filter is null, treats filter as blank`() {
//        val sort = MutableLiveData(ReminderSortOrder.EARLIEST_DATE_FIRST)
//        val filter = MutableLiveData<String>(null)
//
//        val nullFilterReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()
//
//        assertThat(nullFilterReminders).isEqualTo(reminders.sortedBy { it.startDateTime })
//    }
//
//    @Test
//    fun `Filter is only whitespace, treats filter as blank`() {
//        val sort = MutableLiveData(ReminderSortOrder.EARLIEST_DATE_FIRST)
//        val filter = MutableLiveData("      ")
//
//        val whitespaceFilterReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()
//
//        assertThat(whitespaceFilterReminders).isEqualTo(reminders.sortedBy { it.startDateTime })
//    }
//
//    @Test
//    fun `Sort and filter with valid result, returns valid result`() {
//        val sort = MutableLiveData(ReminderSortOrder.EARLIEST_DATE_FIRST)
//        val filter = MutableLiveData("pastReminder")
//
//        val sortFilterReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()
//
//        assertThat(sortFilterReminders).isEqualTo(reminders.subList(0, 1))
//    }
//
//    @Test
//    fun `Sort and filter results in empty list, returns empty list`() {
//        val sort = MutableLiveData(ReminderSortOrder.EARLIEST_DATE_FIRST)
//        val filter = MutableLiveData("x")
//
//        val sortFilterReminders = viewModel.getReminders(sort, filter).getOrAwaitValue()
//
//        assertThat(sortFilterReminders).isEmpty()
//    }
//}

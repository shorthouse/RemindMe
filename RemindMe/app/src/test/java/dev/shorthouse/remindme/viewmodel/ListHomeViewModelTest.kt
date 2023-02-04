//package dev.shorthouse.remindme.viewmodel
//
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.google.common.truth.Truth.assertThat
//import dev.shorthouse.remindme.utilities.enums.ReminderList
//import dev.shorthouse.remindme.utilities.enums.ReminderSortOrder
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@RunWith(AndroidJUnit4::class)
//class ListHomeViewModelTest {
//    private lateinit var listHomeViewModel: ListHomeViewModel
//
//    @Before
//    fun setup() {
//        listHomeViewModel = ListHomeViewModel()
//    }
//
//    @Test
//    fun `Set sort index to 0, sort order is set to earliest date first`() {
//        listHomeViewModel.selectedSortItemIndex = 0
//        val expectedSortOrder = ReminderSortOrder.EARLIEST_DATE_FIRST
//
//        val sortOrder = listHomeViewModel.selectedReminderSortOrder
//
//        assertThat(sortOrder).isEqualTo(expectedSortOrder)
//    }
//
//    @Test
//    fun `Set sort index to 1, sort order is set to latest date first`() {
//        listHomeViewModel.selectedSortItemIndex = 1
//        val expectedSortOrder = ReminderSortOrder.LATEST_DATE_FIRST
//
//        val sortOrder = listHomeViewModel.selectedReminderSortOrder
//
//        assertThat(sortOrder).isEqualTo(expectedSortOrder)
//    }
//
//    @Test
//    fun `Set navigate index to 0, list becomes overdue reminders`() {
//        listHomeViewModel.selectedNavigateItemIndex = 0
//        val expectedReminderList = ReminderList.OVERDUE
//
//        val reminderList = listHomeViewModel.selectedReminderList
//
//        assertThat(reminderList).isEqualTo(expectedReminderList)
//    }
//
//    @Test
//    fun `Set navigate index to 1, list becomes scheduled reminders`() {
//        listHomeViewModel.selectedNavigateItemIndex = 1
//        val expectedReminderList = ReminderList.SCHEDULED
//
//        val reminderList = listHomeViewModel.selectedReminderList
//
//        assertThat(reminderList).isEqualTo(expectedReminderList)
//    }
//
//    @Test
//    fun `Set navigate index to 2, list becomes completed reminders`() {
//        listHomeViewModel.selectedNavigateItemIndex = 2
//        val expectedReminderList = ReminderList.COMPLETED
//
//        val reminderList = listHomeViewModel.selectedReminderList
//
//        assertThat(reminderList).isEqualTo(expectedReminderList)
//    }
//}

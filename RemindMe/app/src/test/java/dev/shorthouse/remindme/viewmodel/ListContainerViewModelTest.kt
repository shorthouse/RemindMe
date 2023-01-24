//package dev.shorthouse.remindme.viewmodel
//
//class ListContainerViewModelTest {
//    // Class under test
//    private val viewModel = ListContainerViewModel()
//
//    @Test
//    fun `Reminder sort to item map, has expected map values`() {
//        val expectedMap = mapOf(
//            ReminderSortOrder.EARLIEST_DATE_FIRST to R.id.drawer_sort_earliest_date_first,
//            ReminderSortOrder.LATEST_DATE_FIRST to R.id.drawer_sort_latest_date_first
//        )
//
//        assertThat(viewModel.sortToMenuItemMap).isEqualTo(expectedMap)
//    }
//
//    @Test
//    fun `Reminder menu item to sort map, has expected values`() {
//        val expectedMap = mapOf(
//            R.id.drawer_sort_earliest_date_first to ReminderSortOrder.EARLIEST_DATE_FIRST,
//            R.id.drawer_sort_latest_date_first to ReminderSortOrder.LATEST_DATE_FIRST
//        )
//
//        assertThat(viewModel.menuItemToSortMap).isEqualTo(expectedMap)
//    }
//
//    @Test
//    fun `Is item changed with equal items, returns false`() {
//        val itemOneId = 1
//        val itemTwoId = 1
//
//        val isItemChanged = viewModel.isItemChanged(itemOneId, itemTwoId)
//
//        assertThat(isItemChanged).isFalse()
//    }
//
//    @Test
//    fun `Is item changed with different items, returns true`() {
//        val itemOneId = 1
//        val itemTwoId = 2
//
//        val isItemChanged = viewModel.isItemChanged(itemOneId, itemTwoId)
//
//        assertThat(isItemChanged).isTrue()
//    }
//
//    @Test
//    fun `Get toolbar title for active reminders, returns expected title`() {
//        val expectedToolbarTitle = R.string.active_reminders
//        viewModel.bottomSheetListSelection = R.id.drawer_active_list
//
//        val toolbarTitle = viewModel.getToolbarTitle()
//
//        assertThat(toolbarTitle).isEqualTo(expectedToolbarTitle)
//    }
//
//    @Test
//    fun `Get toolbar title for all reminders, returns expected title`() {
//        val expectedToolbarTitle = R.string.all_reminders
//        viewModel.bottomSheetListSelection = R.id.drawer_all_list
//
//        val toolbarTitle = viewModel.getToolbarTitle()
//
//        assertThat(toolbarTitle).isEqualTo(expectedToolbarTitle)
//    }
//}

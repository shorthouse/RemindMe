package dev.shorthouse.remindme.viewmodel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReminderListViewPagerViewModelTest {
    // Class under test
    private val viewModel = ReminderListViewPagerViewModel()

    @Test
    fun `menu item to sort map, is correctly mapped`() {
        assertThat(viewModel.sortToMenuItemMap.keys.toString())
            .isEqualTo(viewModel.menuItemToSortMap.values.toString())

        assertThat(viewModel.sortToMenuItemMap.values.toString())
            .isEqualTo(viewModel.menuItemToSortMap.keys.toString())
    }
}
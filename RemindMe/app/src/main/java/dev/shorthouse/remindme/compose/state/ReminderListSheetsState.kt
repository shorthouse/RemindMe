package dev.shorthouse.remindme.compose.state

import androidx.compose.runtime.*
import dev.shorthouse.remindme.util.enums.ReminderBottomSheet
import dev.shorthouse.remindme.util.enums.ReminderList
import dev.shorthouse.remindme.util.enums.ReminderSortOrder

fun ReminderListSheetsState(
    selectedSheet: ReminderBottomSheet,
    selectedReminderListIndex: Int,
    selectedReminderSortOrder: ReminderSortOrder
): ReminderListSheetsState = ReminderListSheetsStateImpl(
    selectedSheet = selectedSheet,
    selectedReminderListIndex = selectedReminderListIndex,
    selectedReminderSortOrder = selectedReminderSortOrder
)

@Stable
interface ReminderListSheetsState {
    var selectedSheet: ReminderBottomSheet
    var selectedReminderListIndex: Int
    val selectedReminderList: ReminderList
    var selectedReminderSortOrder: ReminderSortOrder
}

private class ReminderListSheetsStateImpl(
    selectedSheet: ReminderBottomSheet,
    selectedReminderListIndex: Int,
    selectedReminderSortOrder: ReminderSortOrder
) : ReminderListSheetsState {
    private var _selectedSheet by mutableStateOf(selectedSheet)
    override var selectedSheet: ReminderBottomSheet
        get() = _selectedSheet
        set(value) {
            _selectedSheet = value
        }

    private var _selectedReminderListIndex by mutableStateOf(selectedReminderListIndex)
    override var selectedReminderListIndex: Int
        get() = _selectedReminderListIndex
        set(value) {
            _selectedReminderListIndex = value
        }

    private val _selectedReminderList by derivedStateOf(
        policy = structuralEqualityPolicy(),
        calculation = {
            when (_selectedReminderListIndex) {
                0 -> ReminderList.SCHEDULED
                else -> ReminderList.COMPLETED
            }
        }
    )
    override val selectedReminderList: ReminderList
        get() = _selectedReminderList

    private var _selectedReminderSortOrder by mutableStateOf(selectedReminderSortOrder)
    override var selectedReminderSortOrder: ReminderSortOrder
        get() = _selectedReminderSortOrder
        set(value) {
            _selectedReminderSortOrder = value
        }
}

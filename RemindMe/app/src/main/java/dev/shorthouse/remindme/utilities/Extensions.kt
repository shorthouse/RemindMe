package dev.shorthouse.remindme.utilities

import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView

fun BottomSheetBehavior<NavigationView>.hide() {
    this.state = BottomSheetBehavior.STATE_HIDDEN
}

fun BottomSheetBehavior<NavigationView>.show() {
    this.state = BottomSheetBehavior.STATE_EXPANDED
}

fun BottomSheetBehavior<NavigationView>.isShown(): Boolean {
    return this.state == BottomSheetBehavior.STATE_EXPANDED
}
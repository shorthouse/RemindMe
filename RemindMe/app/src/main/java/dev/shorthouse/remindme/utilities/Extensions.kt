package dev.shorthouse.remindme.utilities

import android.os.SystemClock
import android.view.View
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

fun View.setOnClickThrottleListener(throttleDuration: Long = 1000L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0L

        override fun onClick(view: View) {
            val timeSinceLastClick = SystemClock.elapsedRealtime() - lastClickTime

            if (timeSinceLastClick < throttleDuration) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}


package dev.shorthouse.remindme.utilities

import android.app.Activity
import android.content.Context
import android.os.SystemClock
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun BottomSheetBehavior<NavigationView>.hide() {
    this.state = BottomSheetBehavior.STATE_HIDDEN
}

fun BottomSheetBehavior<NavigationView>.show() {
    this.state = BottomSheetBehavior.STATE_EXPANDED
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


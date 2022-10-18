package dev.shorthouse.remindme.util

import android.view.View
import android.widget.TextView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

fun setTextInTextView(value: String?): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return allOf(
                isDisplayed(),
                isAssignableFrom(TextView::class.java)
            )
        }

        override fun perform(uiController: UiController?, view: View) {
            (view as TextView).text = value
        }

        override fun getDescription(): String {
            return "replace text"
        }
    }
}

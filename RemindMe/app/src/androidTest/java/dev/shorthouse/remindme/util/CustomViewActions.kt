package dev.shorthouse.remindme.util

import android.view.View
import android.widget.TextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.core.AllOf

fun setTextInTextView(value: String?): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return AllOf.allOf(
                ViewMatchers.isDisplayed(),
                ViewMatchers.isAssignableFrom(TextView::class.java)
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

fun checkToastDisplayed(message: String, decorView: View) {
    Espresso.onView(ViewMatchers.withText(message))
        .inRoot(RootMatchers.withDecorView(Matchers.not(decorView)))
        .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
}

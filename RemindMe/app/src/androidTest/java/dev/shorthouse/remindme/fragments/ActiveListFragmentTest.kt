package dev.shorthouse.remindme.fragments

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import dev.shorthouse.remindme.FakeDataSource
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.data.ReminderDataSource
import dev.shorthouse.remindme.model.RepeatInterval
import dev.shorthouse.remindme.di.DataSourceModule
import dev.shorthouse.remindme.launchFragmentInHiltContainer
import dev.shorthouse.remindme.util.TestUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.allOf
import org.junit.Rule
import org.junit.Test
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@HiltAndroidTest
@UninstallModules(DataSourceModule::class)
class ActiveListFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Module
    @InstallIn(SingletonComponent::class)
    class TestModule {
        @Singleton
        @Provides
        fun provideReminderDataSource(): ReminderDataSource {
            val reminders = mutableListOf(
                TestUtil.createReminder(
                    name = "Test Active Reminder",
                    startDateTime = ZonedDateTime.parse("2000-01-01T14:02:00Z"),
                    isNotificationSent = true,
                    repeatInterval = RepeatInterval(1, ChronoUnit.DAYS)
                )
            )

            return FakeDataSource(reminders)
        }
    }

    @Test
    fun when_active_reminder_exists_should_display_correctly() {
        launchFragmentInHiltContainer<ActiveListFragment>()

        onView(withId(R.id.reminder_name)).check(matches(withText("Test Active Reminder")))
        onView(withId(R.id.reminder_date)).check(matches(withText("01 Jan 2000")))
        onView(withId(R.id.reminder_time)).check(matches(withText("14:02")))
        onView(withId(R.id.done_checkbox)).check(matches(allOf(isClickable(), isDisplayed())))
        onView(withId(R.id.notification_icon)).check(matches(isDisplayed()))
        onView(withId(R.id.repeat_icon)).check(matches(isDisplayed()))
    }
}

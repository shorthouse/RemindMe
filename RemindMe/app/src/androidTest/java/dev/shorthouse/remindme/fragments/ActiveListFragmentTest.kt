package dev.shorthouse.remindme.fragments

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
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
import dev.shorthouse.remindme.di.DataSourceModule
import dev.shorthouse.remindme.launchFragmentInHiltContainer
import dev.shorthouse.remindme.util.TestUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import java.time.ZonedDateTime
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
                    startDateTime = ZonedDateTime.parse("2000-01-01T14:02:00Z")
                )
            )

            return FakeDataSource(reminders)
        }
    }

    @Test
    fun when_active_reminder_exists_should_display_correctly()  {
        launchFragmentInHiltContainer<ActiveListFragment>(
            themeResId = R.style.Theme_RemindMe
        )

        onView(withId(R.id.reminder_name)).check(matches(withText("Test Active Reminder")))
        onView(withId(R.id.reminder_date)).check(matches(withText("01 Jan 2000")))
        onView(withId(R.id.reminder_time)).check(matches(withText("14:02")))
    }

    @Test
    fun when_reminder_done_button_clicked_should_display_snackbar() {
        launchFragmentInHiltContainer<ActiveListFragment>(
            themeResId = R.style.Theme_RemindMe
        )

        onView(withText("Done")).perform(click())
        onView(withText("Reminder completed")).check(matches(isDisplayed()))
    }
}
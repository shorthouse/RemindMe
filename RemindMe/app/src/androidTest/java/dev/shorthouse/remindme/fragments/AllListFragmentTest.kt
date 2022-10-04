package dev.shorthouse.remindme.fragments

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
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
class AllListFragmentTest {

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
                    name = "Test Reminder",
                    startDateTime = ZonedDateTime.parse("3000-01-01T09:07:00Z")
                )
            )

            return FakeDataSource(reminders)
        }
    }

    @Test
    fun when_all_reminder_exists_should_display_correctly() {
        launchFragmentInHiltContainer<AllListFragment>(
            themeResId = R.style.Theme_RemindMe
        )

        onView(withId(R.id.reminder_name)).check(matches(withText("Test Reminder")))
        onView(withId(R.id.reminder_date)).check(matches(withText("01 Jan 3000")))
        onView(withId(R.id.reminder_time)).check(matches(withText("09:07")))
    }
}
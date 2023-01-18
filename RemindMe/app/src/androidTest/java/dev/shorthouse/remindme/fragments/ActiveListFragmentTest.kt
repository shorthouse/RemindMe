package dev.shorthouse.remindme.fragments

//@ExperimentalCoroutinesApi
//@HiltAndroidTest
//@UninstallModules(DataSourceModule::class)
//class ActiveListFragmentTest {
//
//    @get:Rule
//    var hiltRule = HiltAndroidRule(this)
//
//    @Module
//    @InstallIn(SingletonComponent::class)
//    class TestModule {
//        @Singleton
//        @Provides
//        fun provideReminderDataSource(): ReminderDataSource {
//            val reminders = mutableListOf(
//                TestUtil.createReminder(
//                    name = "Test Active Reminder",
//                    startDateTime = ZonedDateTime.parse("2000-01-01T14:02:00Z"),
//                    isNotificationSent = true,
//                    repeatInterval = RepeatInterval(1, ChronoUnit.DAYS)
//                )
//            )
//
//            return FakeDataSource(reminders)
//        }
//    }
//
//    @Test
//    fun when_active_reminder_exists_should_display_correctly() {
//        launchFragmentInHiltContainer<ListActiveFragment>()
//
//        onView(withId(R.id.reminder_name)).check(matches(withText("Test Active Reminder")))
//        onView(withId(R.id.reminder_date)).check(matches(withText("01 Jan 2000")))
//        onView(withId(R.id.reminder_time)).check(matches(withText("14:02")))
//        onView(withId(R.id.done_checkbox)).check(matches(allOf(isClickable(), isDisplayed())))
//        onView(withId(R.id.notification_icon)).check(matches(isDisplayed()))
//        onView(withId(R.id.repeat_icon)).check(matches(isDisplayed()))
//    }
//}

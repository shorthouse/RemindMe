package dev.shorthouse.remindme.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.shorthouse.remindme.data.ReminderDataSource
import dev.shorthouse.remindme.data.source.local.ReminderDao
import dev.shorthouse.remindme.data.source.local.ReminderLocalDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {
    @Singleton
    @Provides
    fun provideReminderDataSource(reminderDao: ReminderDao): ReminderDataSource {
        return ReminderLocalDataSource(reminderDao)
    }
}
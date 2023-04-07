package dev.shorthouse.remindme.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.shorthouse.remindme.data.ReminderDataSource
import dev.shorthouse.remindme.data.source.local.ReminderDao
import dev.shorthouse.remindme.data.source.local.ReminderDatabase
import dev.shorthouse.remindme.data.source.local.ReminderRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun provideReminderDatabase(@ApplicationContext context: Context): ReminderDatabase {
        return ReminderDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideReminderDao(reminderDatabase: ReminderDatabase): ReminderDao {
        return reminderDatabase.reminderDao()
    }

    @Singleton
    @Provides
    fun provideReminderRepository(reminderDataSource: ReminderDataSource): ReminderRepository {
        return ReminderRepository(reminderDataSource)
    }
}

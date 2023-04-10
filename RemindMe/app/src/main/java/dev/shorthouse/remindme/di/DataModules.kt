package dev.shorthouse.remindme.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.shorthouse.remindme.data.ReminderDataSource
import dev.shorthouse.remindme.data.source.local.ReminderDao
import dev.shorthouse.remindme.data.source.local.ReminderDatabase
import dev.shorthouse.remindme.data.source.local.ReminderLocalDataSource
import dev.shorthouse.remindme.data.source.local.ReminderRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Singleton
    @Binds
    abstract fun bindLocalDataSource(dataSource: ReminderLocalDataSource): ReminderDataSource
}

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideReminderRepository(reminderDataSource: ReminderDataSource): ReminderRepository {
        return ReminderRepository(reminderDataSource)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): ReminderDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ReminderDatabase::class.java,
            "Reminders.db"
        ).build()
    }

    @Provides
    fun provideReminderDao(database: ReminderDatabase): ReminderDao = database.reminderDao()
}

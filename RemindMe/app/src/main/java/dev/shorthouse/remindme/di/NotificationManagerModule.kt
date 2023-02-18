package dev.shorthouse.remindme.di

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NotificationManagerModule {
    @Singleton
    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManagerCompat {
        return NotificationManagerCompat.from(context)
    }
}

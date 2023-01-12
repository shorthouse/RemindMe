package dev.shorthouse.remindme.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.shorthouse.remindme.utilities.SnackbarDelegate
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SnackbarDelegateModule {
    @Singleton
    @Provides
    fun provideSnackbarDelegate(): SnackbarDelegate {
        return SnackbarDelegate()
    }
}

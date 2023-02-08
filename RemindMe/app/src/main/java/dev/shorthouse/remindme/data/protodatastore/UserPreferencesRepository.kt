package dev.shorthouse.remindme.data.protodatastore

import android.util.Log
import androidx.datastore.core.DataStore
import dev.shorthouse.remindme.protodatastore.UserPreferences
import dev.shorthouse.remindme.protodatastore.UserPreferences.ReminderSortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.io.IOException
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val userPreferencesDataStore: DataStore<UserPreferences>
) {
    private val TAG: String = "UserPreferencesRepo"

    val userPreferencesFlow: Flow<UserPreferences> = userPreferencesDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(TAG, "Error reading sort order preferences.", exception)
                emit(UserPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun updateReminderSortOrder(reminderSortOrder: ReminderSortOrder) {
        userPreferencesDataStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setReminderSortOrder(reminderSortOrder).build()
        }
    }

    suspend fun fetchInitialPreferences() = userPreferencesDataStore.data.first()
}

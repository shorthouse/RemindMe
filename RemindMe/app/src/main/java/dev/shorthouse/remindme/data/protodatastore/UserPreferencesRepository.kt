package dev.shorthouse.remindme.data.protodatastore

import android.util.Log
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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
                emit(UserPreferences())
            } else {
                throw exception
            }
        }

    suspend fun updateReminderSortOrder(reminderSortOrder: ReminderSortOrder) {
        userPreferencesDataStore.updateData { userPreferences ->
            userPreferences.copy(reminderSortOrder = reminderSortOrder)
        }
    }
}

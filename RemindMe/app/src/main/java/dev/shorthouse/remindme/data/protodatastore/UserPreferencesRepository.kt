package dev.shorthouse.remindme.data.protodatastore

import android.util.Log
import androidx.datastore.core.DataStore
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class UserPreferencesRepository @Inject constructor(
    private val userPreferencesDataStore: DataStore<UserPreferences>
) {
    val userPreferencesFlow: Flow<UserPreferences> = userPreferencesDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(
                    "UserPreferencesRepo",
                    "Error reading sort order preferences.",
                    exception
                )
                emit(UserPreferences())
            } else {
                throw exception
            }
        }

    suspend fun updateReminderFilter(reminderFilter: ReminderFilter) {
        userPreferencesDataStore.updateData { userPreferences ->
            userPreferences.copy(reminderFilter = reminderFilter)
        }
    }

    suspend fun updateReminderSortOrder(reminderSortOrder: ReminderSort) {
        userPreferencesDataStore.updateData { userPreferences ->
            userPreferences.copy(reminderSortOrder = reminderSortOrder)
        }
    }
}

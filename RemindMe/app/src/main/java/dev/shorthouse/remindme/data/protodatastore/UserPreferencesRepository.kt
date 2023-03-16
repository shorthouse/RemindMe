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
    val userPreferencesFlow: Flow<UserPreferences> = userPreferencesDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("UserPreferencesRepo", "Error reading sort order preferences.", exception)
                emit(UserPreferences())
            } else {
                throw exception
            }
        }

    suspend fun updateReminderSortOrder(reminderSortOrder: ReminderSort) {
        userPreferencesDataStore.updateData { userPreferences ->
            userPreferences.copy(reminderSortOrder = reminderSortOrder)
        }
    }

    suspend fun toggleReminderFilter(reminderFilter: ReminderFilter) {
        userPreferencesDataStore.updateData { userPreferences ->
            val reminderFilters = userPreferences.reminderFilters.toMutableSet()

            if (reminderFilters.contains(reminderFilter)) {
                reminderFilters.remove(reminderFilter)
            } else {
                reminderFilters.add(reminderFilter)
            }

            userPreferences.copy(reminderFilters = reminderFilters)
        }
    }
}

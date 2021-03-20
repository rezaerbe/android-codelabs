package com.erbe.protodatastore.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.createDataStore
import androidx.datastore.migrations.SharedPreferencesMigration
import androidx.datastore.migrations.SharedPreferencesView
import com.erbe.protodatastore.UserPreferences
import com.erbe.protodatastore.UserPreferences.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

private const val USER_PREFERENCES_NAME = "user_preferences"
private const val DATA_STORE_FILE_NAME = "user_prefs.pb"
private const val SORT_ORDER_KEY = "sort_order"

/**
 * Class that handles saving and retrieving user preferences
 */
class UserPreferencesRepository private constructor(context: Context) {

    private val TAG: String = "UserPreferencesRepo"

    private val sharedPrefsMigration = SharedPreferencesMigration(
            context,
            USER_PREFERENCES_NAME
    ) { sharedPrefs: SharedPreferencesView, currentData: UserPreferences ->
        // Define the mapping from SharedPreferences to UserPreferences
        if (currentData.sortOrder == SortOrder.UNSPECIFIED) {
            currentData.toBuilder().setSortOrder(
                    SortOrder.valueOf(
                            sharedPrefs.getString(
                                    SORT_ORDER_KEY, SortOrder.NONE.name
                            )!!
                    )
            ).build()
        } else {
            currentData
        }
    }

    // Build the DataStore
    private val userPreferencesStore: DataStore<UserPreferences> = context.createDataStore(
            fileName = DATA_STORE_FILE_NAME,
            serializer = UserPreferencesSerializer,
            migrations = listOf(sharedPrefsMigration)
    )

    val userPreferencesFlow: Flow<UserPreferences> = userPreferencesStore.data
            .catch { exception ->
                // dataStore.data throws an IOException when an error is encountered when reading data
                if (exception is IOException) {
                    Log.e(TAG, "Error reading sort order preferences.", exception)
                    emit(UserPreferences.getDefaultInstance())
                } else {
                    throw exception
                }
            }

    /**
     * Enable / disable sort by deadline.
     */
    suspend fun enableSortByDeadline(enable: Boolean) {
        // updateData handles data transactionally, ensuring that if the sort is updated at the same
        // time from another thread, we won't have conflicts
        userPreferencesStore.updateData { currentPreferences ->
            val currentOrder = currentPreferences.sortOrder
            val newSortOrder =
                    if (enable) {
                        if (currentOrder == SortOrder.BY_PRIORITY) {
                            SortOrder.BY_DEADLINE_AND_PRIORITY
                        } else {
                            SortOrder.BY_DEADLINE
                        }
                    } else {
                        if (currentOrder == SortOrder.BY_DEADLINE_AND_PRIORITY) {
                            SortOrder.BY_PRIORITY
                        } else {
                            SortOrder.NONE
                        }
                    }
            currentPreferences.toBuilder().setSortOrder(newSortOrder).build()
        }
    }

    /**
     * Enable / disable sort by priority.
     */
    suspend fun enableSortByPriority(enable: Boolean) {
        // updateData handles data transactionally, ensuring that if the sort is updated at the same
        // time from another thread, we won't have conflicts
        userPreferencesStore.updateData { currentPreferences ->
            val currentOrder = currentPreferences.sortOrder
            val newSortOrder =
                    if (enable) {
                        if (currentOrder == SortOrder.BY_DEADLINE) {
                            SortOrder.BY_DEADLINE_AND_PRIORITY
                        } else {
                            SortOrder.BY_PRIORITY
                        }
                    } else {
                        if (currentOrder == SortOrder.BY_DEADLINE_AND_PRIORITY) {
                            SortOrder.BY_DEADLINE
                        } else {
                            SortOrder.NONE
                        }
                    }
            currentPreferences.toBuilder().setSortOrder(newSortOrder).build()
        }
    }

    suspend fun updateShowCompleted(completed: Boolean) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setShowCompleted(completed).build()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferencesRepository? = null

        fun getInstance(context: Context): UserPreferencesRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferencesRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
}
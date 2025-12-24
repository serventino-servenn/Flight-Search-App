package com.example.flightsearch.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map



interface SearchPreferencesRepository {
    val searchQuery: Flow<String>
    suspend fun saveSearchQuery(query: String)
    suspend fun clearSearchQuery()
}

class OfflineSearchPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) : SearchPreferencesRepository {

    override val searchQuery: Flow<String> =
        dataStore.data.map { preferences ->
            preferences[UserPreferencesKeys.SEARCH_QUERY] ?: ""
        }

    override suspend fun saveSearchQuery(query: String) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.SEARCH_QUERY] = query
        }
    }

    override suspend fun clearSearchQuery() {
        dataStore.edit { preferences ->
            preferences.remove(UserPreferencesKeys.SEARCH_QUERY)
        }
    }
}

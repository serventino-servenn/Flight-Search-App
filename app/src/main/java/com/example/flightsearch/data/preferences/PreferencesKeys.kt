package com.example.flightsearch.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore


val Context.dataStore by preferencesDataStore(name = "user_preferences")
object UserPreferencesKeys  {
    val SEARCH_QUERY = stringPreferencesKey("last_search_query")
}
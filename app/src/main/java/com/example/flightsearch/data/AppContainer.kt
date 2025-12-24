package com.example.flightsearch.data

import android.content.Context
import com.example.flightsearch.data.favorite.FavoriteRepository
import com.example.flightsearch.data.favorite.OfflineFavoriteRepository
import com.example.flightsearch.data.flights.AirportRepository
import com.example.flightsearch.data.flights.OfflineAirportRepository
import com.example.flightsearch.data.preferences.OfflineSearchPreferencesRepository
import com.example.flightsearch.data.preferences.SearchPreferencesRepository
import com.example.flightsearch.data.preferences.dataStore


interface AppContainer{
    val favoriteRepository: FavoriteRepository
    val airportRepository: AirportRepository

    val userPreferencesRepository: SearchPreferencesRepository

}

class DefaultAppContainer(
    private val context: Context
) : AppContainer {

    private val database: AirportDatabase by lazy {
        AirportDatabase.getDatabase(context)
    }

    override val airportRepository: AirportRepository by lazy {
        OfflineAirportRepository(database.airportDao())
    }

    override val favoriteRepository: FavoriteRepository by lazy {
        OfflineFavoriteRepository(database.favoriteDao())
    }

    override val userPreferencesRepository: SearchPreferencesRepository by lazy {
        OfflineSearchPreferencesRepository(context.dataStore)
    }
}

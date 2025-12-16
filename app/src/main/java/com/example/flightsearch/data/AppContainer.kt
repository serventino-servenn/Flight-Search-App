package com.example.flightsearch.data

import android.content.Context
import com.example.flightsearch.data.favorite.FavoriteRepository
import com.example.flightsearch.data.favorite.OfflineFavoriteRepository
import com.example.flightsearch.data.flights.AirportRepository
import com.example.flightsearch.data.flights.OfflineAirportRepository


interface AppContainer{
    val favoriteRepository: FavoriteRepository
    val airportRepository: AirportRepository

}

class DefaultAppContainer(private val context: Context) : AppContainer {
    private val database: AirportDatabase by lazy {
        AirportDatabase.getDatabase(context)
    }

    override val airportRepository: AirportRepository by lazy {
        OfflineAirportRepository(database.airportDao())
    }
    override val favoriteRepository: FavoriteRepository by lazy {
        OfflineFavoriteRepository(database.favoriteDao())
    }


}
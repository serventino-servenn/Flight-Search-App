package com.example.flightsearch.data.flights

interface AirportRepository {
    suspend fun getDepartures(departureCode:String):List<AirportItem>
    suspend fun searchAirports(query:String):List<AirportItem>
    suspend fun getAllAirports():List<AirportItem>
}
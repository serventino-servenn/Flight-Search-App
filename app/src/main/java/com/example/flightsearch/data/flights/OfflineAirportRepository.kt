package com.example.flightsearch.data.flights

class OfflineAirportRepository(
    private val airportDao: AirportDao
): AirportRepository {
    override suspend fun getDepartures(departureCode: String): List<AirportItem> = airportDao.getDepartures(departureCode)


    override suspend fun searchAirports(query: String): List<AirportItem> = airportDao.searchAirports(query)


    override suspend fun getAllAirports(): List<AirportItem> =  airportDao.getAllAirports()


}
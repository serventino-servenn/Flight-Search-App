package com.example.flightsearch.data.flights

import androidx.room.Dao
import androidx.room.Query

@Dao
interface AirportDao {
    //find flights using IATA code
    @Query("select * from airport where iata_code =:departureCode")
    suspend fun getDepartures(departureCode: String):List<AirportItem>

    //search airport by iata code or name
    @Query("select * from airport where iata_code like:query or name like:query")
    suspend fun searchAirports(query:String):List<AirportItem>

    @Query("Select * from airport")
    suspend fun getAllAirports():List<AirportItem>

}
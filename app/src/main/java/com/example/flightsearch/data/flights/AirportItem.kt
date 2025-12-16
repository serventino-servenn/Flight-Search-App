package com.example.flightsearch.data.flights

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName ="airport")
data class AirportItem(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val iata_code: String,
    val name:String,
    val passengers:Int
)

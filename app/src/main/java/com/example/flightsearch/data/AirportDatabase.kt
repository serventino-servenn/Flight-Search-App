package com.example.flightsearch.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flightsearch.data.favorite.FavoriteDao
import com.example.flightsearch.data.flights.AirportDao
import com.example.flightsearch.data.flights.AirportItem

@Database(
    entities = [
        AirportItem::class,
        com.example.flightsearch.data.favorite.FavoriteItem::class
               ],
    version = 1,
    exportSchema = false
)
abstract class AirportDatabase:RoomDatabase(){
    abstract fun airportDao(): AirportDao
    abstract fun favoriteDao(): FavoriteDao

    companion object{
        @Volatile
        private var Instance: AirportDatabase? = null
        fun getDatabase(context: Context): AirportDatabase{
            return Instance?:synchronized(this){
                Room.databaseBuilder<AirportDatabase>(
                    context.applicationContext,
                    AirportDatabase::class.java,
                    "airport.db"
                )
                    .createFromAsset("flight_search.db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }

            }
        }
    }
}
package com.example.flightsearch.data.favorite

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteItem)

    @Delete
    suspend fun deleteFavorite(favoriteItem: FavoriteItem)

    //get all favorites
    @Query("SELECT * FROM favorite")
    suspend fun getAllFavorites(): List<FavoriteItem>


}
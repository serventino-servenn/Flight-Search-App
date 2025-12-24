package com.example.flightsearch.data.favorite

import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun insertFavorite(favoriteItem: FavoriteItem)
    suspend fun deleteFavoriteByCodes(depCode: String, destCode: String)
//    suspend fun getAllFavorites(): List<FavoriteItem>

    val favorites: Flow<List<FavoriteItem>>
}


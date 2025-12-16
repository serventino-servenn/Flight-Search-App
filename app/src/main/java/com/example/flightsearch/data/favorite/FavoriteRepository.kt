package com.example.flightsearch.data.favorite

import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun insertFavorite(favoriteItem: FavoriteItem)
    suspend fun deleteFavorite(favoriteItem: FavoriteItem)
    suspend fun getAllFavorite(): List<FavoriteItem>
}
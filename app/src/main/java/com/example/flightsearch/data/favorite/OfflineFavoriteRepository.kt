package com.example.flightsearch.data.favorite

import kotlinx.coroutines.flow.Flow

class OfflineFavoriteRepository(
    private val favoriteDao: FavoriteDao
): FavoriteRepository {
    override suspend fun insertFavorite(favoriteItem: FavoriteItem) = favoriteDao.insertFavorite(favoriteItem)

    override suspend fun deleteFavorite(favoriteItem: FavoriteItem) = favoriteDao.deleteFavorite(favoriteItem)

    override suspend fun getAllFavorite() = favoriteDao.getAllFavorites()

}
package com.example.flightsearch.data.favorite

import kotlinx.coroutines.flow.Flow

class OfflineFavoriteRepository(
    private val favoriteDao: FavoriteDao
): FavoriteRepository {
    override suspend fun insertFavorite(favoriteItem: FavoriteItem) =
        favoriteDao.insertFavorite(favoriteItem)

    override suspend fun deleteFavoriteByCodes(depCode: String, destCode: String) {
        favoriteDao.deleteFavoriteByCodes(depCode, destCode)
    }



    override val favorites: Flow<List<FavoriteItem>> =
        favoriteDao.getAllFavorites()

}


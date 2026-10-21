package com.chillsam.courmy.common.domain.favorite

import com.chillsam.courmy.common.entity.favorite.FavoriteItemVO
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getFavoriteItemsFlow(): Flow<List<FavoriteItemVO>>

    suspend fun createFavoriteItem(item: FavoriteItemVO)

    suspend fun deleteFavoriteItem(url: String)
}

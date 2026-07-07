package com.chillsam.courmy.favorite.presentation

import com.chillsam.courmy.common.entity.favorite.FavoriteItemVO
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent

sealed interface FavoriteReducerEvent : ReducerEvent {
    data object LoadingStarted : FavoriteReducerEvent

    data class ItemsLoaded(
        val items: List<FavoriteItemVO>,
    ) : FavoriteReducerEvent
}

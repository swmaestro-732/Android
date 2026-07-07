package com.chillsam.courmy.favorite.presentation

import com.chillsam.courmy.common.presentation.mvi.MviIntent

sealed interface FavoriteIntent : MviIntent {
    data object Load : FavoriteIntent
    data class DeleteFavorite(val url: String) : FavoriteIntent
    data class OpenFullScreen(val url: String) : FavoriteIntent
}

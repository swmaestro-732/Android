package com.chillsam.courmy.fullScreenMedia.presentation

import com.chillsam.courmy.common.entity.media.MediaItemVO
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent

sealed interface FullScreenMediaReducerEvent : ReducerEvent {
    data class Initialized(
        val mediaItems: List<MediaItemVO>,
        val initialIndex: Int,
        val swipeEnabled: Boolean,
    ) : FullScreenMediaReducerEvent

    data object EmptyMediaResolved : FullScreenMediaReducerEvent

    data class FavoritesChanged(
        val urls: Set<String>,
    ) : FullScreenMediaReducerEvent

    data class PageSelected(
        val index: Int,
    ) : FullScreenMediaReducerEvent
}

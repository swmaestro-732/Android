package com.chillsam.courmy.common.entity.favorite

import com.chillsam.courmy.common.entity.media.MediaType

data class FavoriteItemVO(
    val type: MediaType,
    val title: String,
    val urlKey: String,
    val thumbnailUrl: String = "",
    val contentsImageUrl: String = "",
    val dateTime: String = "",
)

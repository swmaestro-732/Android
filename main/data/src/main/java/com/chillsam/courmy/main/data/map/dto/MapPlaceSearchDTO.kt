package com.chillsam.courmy.main.data.map.dto

import com.chillsam.courmy.common.entity.category.toPlaceCategoryLabel
import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import kotlinx.serialization.Serializable

@Serializable
data class MapPlaceSearchEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: MapPlaceSearchDTO? = null,
)

@Serializable
data class MapPlaceSearchDTO(
    val places: List<MapPlaceSearchItemDTO>? = null,
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
)

@Serializable
data class MapPlaceSearchItemDTO(
    val id: Long? = null,
    val name: String? = null,
    val imageUrl: String? = null,
    val categories: List<String>? = null,
    val averageRating: Double? = null,
    val walkingMinutes: Int? = null,
    val location: MapPlaceSearchLocationDTO? = null,
    val hasSaved: Boolean = false,
)

@Serializable
data class MapPlaceSearchLocationDTO(
    val latitude: Double? = null,
    val longitude: Double? = null,
)

internal fun MapPlaceSearchDTO.toPageVO(): CursorPageVO<MapPlaceVO> =
    CursorPageVO(
        items =
            places
                .orEmpty()
                .filter { it.id != null }
                .map { place ->
                    MapPlaceVO(
                        id = requireNotNull(place.id),
                        name = place.name.orEmpty(),
                        category = place.categories?.firstOrNull().toPlaceCategoryLabel(),
                        subCategory =
                            place.categories
                                ?.drop(1)
                                ?.map {
                                    it.toPlaceCategoryLabel()
                                }?.joinToString(" · ")
                                ?.takeIf { it.isNotBlank() },
                        thumbnailUrl = place.imageUrl,
                        rating = place.averageRating ?: 0.0,
                        walkingMinutes = place.walkingMinutes,
                        latitude = place.location?.latitude?.takeIf { place.location.longitude != null },
                        longitude = place.location?.longitude?.takeIf { place.location.latitude != null },
                        saved = place.hasSaved,
                    )
                }.distinctBy { it.id },
        nextCursor = nextCursor?.takeIf { it.isNotBlank() },
        hasNext = hasNext && !nextCursor.isNullOrBlank(),
    )

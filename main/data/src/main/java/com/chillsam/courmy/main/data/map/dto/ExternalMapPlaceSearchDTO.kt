package com.chillsam.courmy.main.data.map.dto

import com.chillsam.courmy.common.entity.category.toPlaceCategoryLabel
import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import kotlinx.serialization.Serializable

/** `GET /api/v1/places/search?query=...` 응답. 검색 결과에는 내부 장소 ID와 평면 좌표가 포함된다. */
@Serializable
data class ExternalMapPlaceSearchEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: ExternalMapPlaceSearchDTO? = null,
)

@Serializable
data class ExternalMapPlaceSearchDTO(
    val places: List<ExternalMapPlaceSearchItemDTO>? = null,
)

@Serializable
data class ExternalMapPlaceSearchItemDTO(
    val id: Long? = null,
    val name: String? = null,
    val category: String? = null,
    val roadAddress: String? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
)

/** 외부 검색은 커서를 제공하지 않는다. 평점·사진·저장 여부는 응답에 없어 기본값을 유지한다. */
internal fun ExternalMapPlaceSearchDTO.toPageVO(): CursorPageVO<MapPlaceVO> =
    CursorPageVO(
        items =
            places
                .orEmpty()
                .filter { (it.id ?: 0) > 0 }
                .map { place ->
                    val address = place.roadAddress?.takeIf { it.isNotBlank() } ?: place.address
                    MapPlaceVO(
                        id = requireNotNull(place.id),
                        name = place.name.orEmpty(),
                        category = place.category.toPlaceCategoryLabel(),
                        subCategory = address?.takeIf { it.isNotBlank() },
                        address = address.orEmpty(),
                        latitude = place.latitude?.takeIf { place.longitude != null },
                        longitude = place.longitude?.takeIf { place.latitude != null },
                    )
                }.distinctBy { it.id },
    )

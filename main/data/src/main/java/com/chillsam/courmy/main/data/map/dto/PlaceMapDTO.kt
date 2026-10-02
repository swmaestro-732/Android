package com.chillsam.courmy.main.data.map.dto

import com.chillsam.courmy.main.entity.map.MapClusterVO
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import com.chillsam.courmy.main.entity.map.MapPlacesVO
import kotlinx.serialization.Serializable

/**
 * `GET /api/v1/places/map` 응답 DTO. 봉투는 공통 계약 `{ code, message, data }`.
 *
 * 마커 응답에 평점·도보 시간·저장 여부·영업 정보가 없어 [MapPlaceVO] 의 해당 필드는 기본값으로 둔다
 * — 지도 핀과 시트 목록의 이름·카테고리까지만 서버 값으로 채운다. [wiki-needed]
 */
@Serializable
data class PlaceMapEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: PlaceMapDTO? = null,
)

@Serializable
data class PlaceMapDTO(
    val totalCount: Long? = null,
    val places: List<PlaceMapMarkerDTO>? = null,
    val clusters: List<PlaceMapClusterDTO>? = null,
)

@Serializable
data class PlaceMapMarkerDTO(
    val id: Long? = null,
    val name: String? = null,
    /** 서버 `PlaceCategory` enum 이름(예: "CAFE"). */
    val category: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
)

@Serializable
data class PlaceMapClusterDTO(
    /** 서버 클러스터 식별 키. 화면이 쓰지 않아 매핑하지 않는다. */
    val key: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val count: Long? = null,
)

internal fun PlaceMapDTO.toVO(): MapPlacesVO =
    MapPlacesVO(
        totalCount = (totalCount ?: 0L).toInt(),
        places =
            places
                .orEmpty()
                // id·좌표가 없으면 핀을 찍을 수도, 상세로 이동할 수도 없다.
                .filter { it.id != null && it.latitude != null && it.longitude != null }
                .map { marker ->
                    MapPlaceVO(
                        id = requireNotNull(marker.id),
                        name = marker.name.orEmpty(),
                        category = marker.category.toPlaceCategoryLabel(),
                        latitude = marker.latitude,
                        longitude = marker.longitude,
                    )
                },
        clusters =
            clusters
                .orEmpty()
                .filter { it.latitude != null && it.longitude != null }
                .map { cluster ->
                    MapClusterVO(
                        latitude = requireNotNull(cluster.latitude),
                        longitude = requireNotNull(cluster.longitude),
                        count = (cluster.count ?: 0L).toInt(),
                    )
                },
    )

/**
 * 서버 `PlaceCategory` enum → 화면 라벨. 지도 칩(FS-10)의 문구와 맞춘다
 * (CULTURE ↔ "전시·갤러리", SHOPPING ↔ "편집숍").
 *
 * 모르는 값은 그대로 노출한다 — 서버에 카테고리가 추가돼도 빈 줄 대신 원문이 보이는 쪽이 낫고,
 * 디버그 폴백이 한국어 라벨을 바로 싣는 경로이기도 하다.
 */
internal fun String?.toPlaceCategoryLabel(): String =
    when (this?.trim()?.uppercase()) {
        null, "" -> ""
        "CAFE" -> "카페"
        "RESTAURANT" -> "맛집"
        "BAR" -> "술집·바"
        "SHOPPING" -> "편집숍"
        "CULTURE" -> "전시·갤러리"
        "EXPERIENCE" -> "체험"
        "NATURE" -> "자연"
        "LANDMARK" -> "명소"
        "ENTERTAINMENT" -> "놀거리"
        "WELLNESS" -> "웰니스"
        else -> this.orEmpty()
    }

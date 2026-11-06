package com.chillsam.courmy.course.data.traces.dto

import com.chillsam.courmy.course.entity.RouteGuideVO
import com.chillsam.courmy.course.entity.RoutePlaceVO
import com.chillsam.courmy.course.entity.RoutePointVO
import com.chillsam.courmy.course.entity.TraceRouteVO
import kotlinx.serialization.Serializable
import kotlin.math.ceil

/**
 * 도보 경로 조회 응답(`GET /api/v1/traces/route?fromPlaceId&toPlaceId`). 봉투는 공통 `{ code, message, data }`.
 *
 * [TraceRouteDataDTO.walkable] 가 false 면 도보 불가 구간이라 거리·시간은 null, path·guides 는 빈 배열이다.
 */
@Serializable
data class TraceRouteEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: TraceRouteDataDTO? = null,
)

@Serializable
data class TraceRouteDataDTO(
    val from: RoutePlaceDTO? = null,
    val to: RoutePlaceDTO? = null,
    val walkable: Boolean? = null,
    val distanceMeters: Int? = null,
    val durationSeconds: Int? = null,
    val durationMinutes: Int? = null,
    val path: List<RoutePointDTO>? = null,
    val guides: List<RouteGuideDTO>? = null,
)

@Serializable
data class RoutePlaceDTO(
    val placeId: Long? = null,
    val name: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
)

@Serializable
data class RoutePointDTO(
    val latitude: Double? = null,
    val longitude: Double? = null,
)

@Serializable
data class RouteGuideDTO(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val description: String? = null,
)

/** VO 변환은 이 data 레이어에서만 수행한다(모듈 관례). */
fun TraceRouteDataDTO?.toVO(): TraceRouteVO {
    val data = this ?: error("도보 경로 응답에 data 가 없습니다.")
    return TraceRouteVO(
        from = data.from.toVO(),
        to = data.to.toVO(),
        // walkable 가 없으면 거리 값 유무로 보수적으로 판단한다.
        walkable = data.walkable ?: (data.distanceMeters != null),
        distanceMeters = data.distanceMeters,
        // 서버가 분을 주지 않고 초만 줄 때를 대비해 초에서 올림으로 환산한다.
        durationMinutes = data.durationMinutes ?: data.durationSeconds?.let { ceil(it / 60.0).toInt() },
        path = data.path.orEmpty().mapNotNull { it.toVOOrNull() },
        guides = data.guides.orEmpty().mapNotNull { it.toVOOrNull() },
    )
}

private fun RoutePlaceDTO?.toVO(): RoutePlaceVO {
    val place = this ?: error("도보 경로 응답에 장소가 없습니다.")
    return RoutePlaceVO(
        placeId = place.placeId ?: 0L,
        name = place.name.orEmpty(),
        latitude = place.latitude ?: 0.0,
        longitude = place.longitude ?: 0.0,
    )
}

private fun RoutePointDTO.toVOOrNull(): RoutePointVO? {
    val latitude = latitude ?: return null
    val longitude = longitude ?: return null
    return RoutePointVO(latitude, longitude)
}

private fun RouteGuideDTO.toVOOrNull(): RouteGuideVO? {
    val latitude = latitude ?: return null
    val longitude = longitude ?: return null
    return RouteGuideVO(latitude, longitude, description.orEmpty())
}

package com.chillsam.courmy.course.entity

/**
 * 두 장소 사이 실제 도보 경로(`GET /api/v1/traces/route`). 따라가기 지도에 "현재 → 다음 장소"
 * 보행 경로를 그리고 거리·시간·턴바이턴 안내를 보여 주는 데 쓴다.
 *
 * 세션(traceId)·코스에 묶이지 않는다 — 장소 id 두 개로만 조회하므로 코스 밖 장소에도 쓸 수 있다.
 *
 * - [walkable]       도보로 갈 수 있는 구간인지. false 면 Tmap 서비스 불가(물 위·경로 없음)이며
 *                    [distanceMeters]·[durationMinutes] 는 null, [path]·[guides] 는 빈 목록이다.
 * - [distanceMeters] 총 도보 거리(m). 도보 불가면 null
 * - [durationMinutes] 총 도보 소요(분, 서버가 초를 올림). 화면 표기용. 도보 불가면 null
 * - [path]           지도에 그릴 폴리라인. 출발 → 도착 순서로 이어진 좌표를 그대로 선으로 잇는다
 * - [guides]         출발·회전·횡단보도·도착 안내 지점(경로 순서). 문구는 Tmap 것을 그대로 쓴다
 */
data class TraceRouteVO(
    val from: RoutePlaceVO,
    val to: RoutePlaceVO,
    val walkable: Boolean,
    val distanceMeters: Int? = null,
    val durationMinutes: Int? = null,
    val path: List<RoutePointVO> = emptyList(),
    val guides: List<RouteGuideVO> = emptyList(),
)

/** 경로의 출발·도착 장소. */
data class RoutePlaceVO(
    val placeId: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
)

/** 폴리라인 좌표 한 점. */
data class RoutePointVO(
    val latitude: Double,
    val longitude: Double,
)

/** 턴바이턴 안내 지점: 좌표 + 안내 문구(예: "이마트24 강남역점에서 우회전 후 17m 이동"). */
data class RouteGuideVO(
    val latitude: Double,
    val longitude: Double,
    val description: String,
)

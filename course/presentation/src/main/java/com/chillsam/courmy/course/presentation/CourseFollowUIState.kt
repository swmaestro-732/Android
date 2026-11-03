package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.course.entity.CourseDetailVO
import com.chillsam.courmy.course.entity.TraceRouteVO
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf

/**
 * 따라가기 진행 단계(FS-21).
 *
 * - [PREVIEW]  동선 미리보기. 지도와 장소 순서를 훑고 "이 코스 따라가기" 로 시작한다.
 * - [TRACKING] 진행 중. 지금 가야 할 장소가 하나 있고, 도착하면 방문 체크로 다음으로 넘어간다.
 * - [DONE]     완주. 통계를 보여 주고 후기 작성으로 유도한다.
 */
enum class CourseFollowPhase { PREVIEW, TRACKING, DONE }

/**
 * 구간(현재 장소 → 다음 장소)별 도보 경로 상태. 지도에 실제 경로를 그리고, 못 그릴 때 외부 지도로 넘길지 가른다.
 *
 * - [Loading] 조회 중. 지도는 임시로 직선으로 잇는다.
 * - [Ready]   조회 완료. [route] 의 `walkable` 이 false 면 도보 불가 구간이다(경로 좌표 없음).
 * - [Failed]  조회 실패(Tmap 503 등). 외부 지도(네이버 지도) 길안내로 폴백한다.
 */
sealed interface FollowRouteStatus {
    data object Loading : FollowRouteStatus

    data class Ready(
        val route: TraceRouteVO,
    ) : FollowRouteStatus

    data object Failed : FollowRouteStatus
}

/**
 * 따라가기 화면 상태(FS-21).
 *
 * 방문 진행은 화면 세션 상태이며 시작·방문·종료 기록은 순서대로 서버에 보낸다.
 *
 * - [currentIndex]   지금 가야 할 장소의 [CourseDetailVO.places] 인덱스
 * - [visitedCount]   방문 체크를 마친 장소 수. 인덱스보다 수가 믿을 만하다(건너뛰기가 생겨도 유지).
 * - [startedAtMillis]/[finishedAtMillis] 소요 시간 계산용. 완주 화면에서만 쓴다.
 * - [routes]         구간 도보 경로를 **출발 장소 placeId** 로 색인한다. 로드 직후 전 구간을 병렬 조회해 채운다.
 */
data class CourseFollowUIState(
    val isLoading: Boolean = true,
    val isRecording: Boolean = false,
    val detail: CourseDetailVO? = null,
    val errorMessage: String? = null,
    val phase: CourseFollowPhase = CourseFollowPhase.PREVIEW,
    val currentIndex: Int = 0,
    val visitedCount: Int = 0,
    val startedAtMillis: Long? = null,
    val finishedAtMillis: Long? = null,
    val routes: PersistentMap<Long, FollowRouteStatus> = persistentMapOf(),
) : UiState {
    /** 지금 가야 할 장소. 진행 전·완주 후에는 화면이 쓰지 않는다. */
    val currentPlace get() = detail?.places?.getOrNull(currentIndex)

    /** 다음에 갈 장소. 마지막 장소를 보고 있으면 null. */
    val nextPlace get() = detail?.places?.getOrNull(currentIndex + 1)

    /** 지금 가야 할 장소에서 다음 장소까지의 도보 경로 상태. 마지막 장소면 null. */
    val currentRouteStatus: FollowRouteStatus?
        get() = if (nextPlace == null) null else currentPlace?.let { routes[it.placeId] }

    companion object {
        val empty: CourseFollowUIState = CourseFollowUIState()
    }
}

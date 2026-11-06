package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.course.entity.CourseDetailVO
import com.chillsam.courmy.course.entity.TraceRouteVO

/** 따라가기 화면 상태 변이 이벤트(FS-21). [CourseFollowViewModel.reduce] 한 곳에서만 소비한다. */
sealed interface CourseFollowReducerEvent : ReducerEvent {
    data object LoadStarted : CourseFollowReducerEvent

    data class Loaded(
        val detail: CourseDetailVO,
    ) : CourseFollowReducerEvent

    data class LoadFailed(
        val message: String,
    ) : CourseFollowReducerEvent

    /** 진행 시작. [startedAtMillis] 는 소요 시간 계산의 기준점이다. */
    data class Started(
        val startedAtMillis: Long,
    ) : CourseFollowReducerEvent

    /**
     * 이어가기 가능. 미리보기는 그대로 두고, 이미 방문한 수만 알려 미리보기 버튼을 "이어가기"로 바꾼다.
     * 실제 이어가기(TRACKING 전환)는 사용자가 버튼을 눌러 [Resumed] 로 일어난다.
     */
    data class ResumeOffered(
        val visitedCount: Int,
    ) : CourseFollowReducerEvent

    /**
     * 이어가기. 진행 중 세션을 현재 진행도에서 TRACKING 으로 연다.
     * [currentIndex] 는 다음 가야 할 장소(= 방문 수), [visitedCount] 는 지금까지 방문 수,
     * [startedAtMillis] 는 소요 시간 기준점(서버 startedAt).
     */
    data class Resumed(
        val currentIndex: Int,
        val visitedCount: Int,
        val startedAtMillis: Long,
    ) : CourseFollowReducerEvent

    /** 지금 장소 방문 처리. 마지막 장소였으면 [finishedAtMillis] 가 담겨 완주로 넘어간다. */
    data class Visited(
        val finishedAtMillis: Long?,
    ) : CourseFollowReducerEvent

    data object RecordingStarted : CourseFollowReducerEvent

    data object ErrorConsumed : CourseFollowReducerEvent

    /** 전 구간 도보 경로 조회를 시작한다. [fromPlaceIds] 는 각 구간 출발 장소 placeId 로 모두 로딩 상태로 둔다. */
    data class RoutesRequested(
        val fromPlaceIds: List<Long>,
    ) : CourseFollowReducerEvent

    /** 한 구간 도보 경로 조회 성공. [route] 는 도보 불가(walkable=false)일 수도 있다. */
    data class RouteLoaded(
        val fromPlaceId: Long,
        val route: TraceRouteVO,
    ) : CourseFollowReducerEvent

    /** 한 구간 도보 경로 조회 실패(Tmap 503 등). 외부 지도 폴백 안내용. */
    data class RouteFailed(
        val fromPlaceId: Long,
    ) : CourseFollowReducerEvent
}

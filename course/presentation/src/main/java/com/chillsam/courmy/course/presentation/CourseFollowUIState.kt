package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.course.entity.CourseDetailVO

/**
 * 따라가기 진행 단계(FS-21).
 *
 * - [PREVIEW]  동선 미리보기. 지도와 장소 순서를 훑고 "이 코스 따라가기" 로 시작한다.
 * - [TRACKING] 진행 중. 지금 가야 할 장소가 하나 있고, 도착하면 방문 체크로 다음으로 넘어간다.
 * - [DONE]     완주. 통계를 보여 주고 후기 작성으로 유도한다.
 */
enum class CourseFollowPhase { PREVIEW, TRACKING, DONE }

/**
 * 따라가기 화면 상태(FS-21).
 *
 * 방문 기록은 화면 세션 상태다 — 서버 따라가기(trace) API 계약이 아직 없어 로컬로만 센다.
 * TODO-API-SPEC: trace 계약이 생기면 시작·방문 체크를 서버에 기록한다.
 *
 * - [currentIndex]   지금 가야 할 장소의 [CourseDetailVO.places] 인덱스
 * - [visitedCount]   방문 체크를 마친 장소 수. 인덱스보다 수가 믿을 만하다(건너뛰기가 생겨도 유지).
 * - [startedAtMillis]/[finishedAtMillis] 소요 시간 계산용. 완주 화면에서만 쓴다.
 */
data class CourseFollowUIState(
    val isLoading: Boolean = true,
    val detail: CourseDetailVO? = null,
    val errorMessage: String? = null,
    val phase: CourseFollowPhase = CourseFollowPhase.PREVIEW,
    val currentIndex: Int = 0,
    val visitedCount: Int = 0,
    val startedAtMillis: Long? = null,
    val finishedAtMillis: Long? = null,
) : UiState {
    /** 지금 가야 할 장소. 진행 전·완주 후에는 화면이 쓰지 않는다. */
    val currentPlace get() = detail?.places?.getOrNull(currentIndex)

    /** 다음에 갈 장소. 마지막 장소를 보고 있으면 null. */
    val nextPlace get() = detail?.places?.getOrNull(currentIndex + 1)

    companion object {
        val empty: CourseFollowUIState = CourseFollowUIState()
    }
}

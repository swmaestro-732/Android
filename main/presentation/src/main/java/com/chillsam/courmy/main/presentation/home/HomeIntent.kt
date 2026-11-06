package com.chillsam.courmy.main.presentation.home

import com.chillsam.courmy.common.presentation.mvi.MviIntent

/** 홈 화면(FS-09) 사용자 입력. 세로 피드 전체(전체보기)의 입력은 [HomeFeedIntent] 가 따로 맡는다. */
sealed interface HomeIntent : MviIntent {
    /** 최초 진입·재진입·에러 재시도 공통. 화면이 RefreshOnResume 으로 매번 부른다. */
    data object Retry : HomeIntent

    /** 기기 현위치가 확보됐을 때. 근처 저장 장소 거리 기준점을 바꾸고 다시 불러온다(SCRUM-634). */
    data class UpdateLocation(
        val lat: Double,
        val lng: Double,
    ) : HomeIntent

    /** 진행 중인 코스 카드의 스톱 버튼. 이번 세션에서 카드를 내린다(다음 홈 로드에 서버가 다시 주면 복원). */
    data object DismissInProgressCourse : HomeIntent

    /** 종료 성공 알림([HomeUIState.inProgressEnded])을 소비한다 — 저장함(완주) 이동 후 화면이 부른다. */
    data object ConsumeInProgressEnded : HomeIntent
}

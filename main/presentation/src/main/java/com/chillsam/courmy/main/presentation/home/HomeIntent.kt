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
}

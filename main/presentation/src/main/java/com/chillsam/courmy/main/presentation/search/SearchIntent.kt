package com.chillsam.courmy.main.presentation.search

import com.chillsam.courmy.common.presentation.mvi.MviIntent

/** 검색어 입력 화면(FS-17)의 사용자 입력. */
sealed interface SearchIntent : MviIntent {
    data object Load : SearchIntent

    data class ChangeQuery(
        val query: String,
    ) : SearchIntent

    /** 입력칸의 지우기(×). 화면을 떠나지 않고 입력만 비운다. */
    data object ClearQuery : SearchIntent

    /** 최근 검색어 한 건 삭제. */
    data class RemoveRecent(
        val keyword: String,
    ) : SearchIntent

    data object ClearAllRecent : SearchIntent

    /**
     * 검색 실행. 최근 검색어에 남기는 것까지가 이 화면의 몫이고,
     * 결과 화면으로 넘어가는 건 Page 가 한다.
     */
    data class Submit(
        val keyword: String,
    ) : SearchIntent
}

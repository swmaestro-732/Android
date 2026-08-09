package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.MviIntent

/** 임시저장 목록 화면의 사용자 입력. */
sealed interface DraftListIntent : MviIntent {
    /** 목록 조회. 화면 진입과 "다시 시도" 모두 이 인텐트를 쓴다(서버 조회라 재시도가 필요하다). */
    data object Load : DraftListIntent

    /** 초안 1건 삭제. 확인 다이얼로그를 거친 뒤에만 들어온다. */
    data class Delete(
        val courseId: Long,
    ) : DraftListIntent

    /** 안내를 노출한 뒤 상태에서 지운다. */
    data object ConsumeError : DraftListIntent
}

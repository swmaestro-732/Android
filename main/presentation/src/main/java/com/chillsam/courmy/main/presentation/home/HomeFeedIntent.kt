package com.chillsam.courmy.main.presentation.home

import com.chillsam.courmy.common.entity.category.CourseTagVO
import com.chillsam.courmy.common.presentation.mvi.MviIntent

/** 홈 공개 코스 피드(FS-09) 사용자 입력. */
sealed interface HomeFeedIntent : MviIntent {
    data object Load : HomeFeedIntent

    data object Retry : HomeFeedIntent

    /** 목록 끝에 닿았을 때 다음 페이지 요청. 더 없거나 이미 받는 중이면 무시된다. */
    data object LoadMore : HomeFeedIntent

    /** 카드 북마크 탭 — 저장/저장 취소를 토글한다. */
    data class ToggleSave(
        val courseId: String,
    ) : HomeFeedIntent

    /**
     * 복귀 시 저장 상태만 동기화한다. 전체 재로드([Retry])는 무한 스크롤로 쌓은 페이지와
     * 스크롤 위치를 날리므로(SCRUM-631), 상세에서 바뀔 수 있는 저장 여부만 새로 받는다.
     */
    data object SyncSavedIds : HomeFeedIntent

    /** 테마 필터 선택 — 해당 카테고리로 피드를 서버에서 다시 받는다. null 은 전체(추천순). */
    data class SelectCategory(
        val category: CourseTagVO?,
    ) : HomeFeedIntent

    data object ConsumeError : HomeFeedIntent
}

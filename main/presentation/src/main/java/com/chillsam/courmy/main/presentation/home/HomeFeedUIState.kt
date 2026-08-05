package com.chillsam.courmy.main.presentation.home

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.entity.home.HomeCourseVO
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

/**
 * 홈 공개 코스 피드 상태.
 *
 * 헤더·하단 탭은 피드와 무관하게 항상 보여야 하므로, 로딩·에러는 화면 전체가 아니라
 * 피드 영역에서만 분기한다([courses] 가 비어 있을 때 [isLoading]/[errorMessage] 로 판단).
 */
data class HomeFeedUIState(
    val isLoading: Boolean = true,
    val courses: ImmutableList<HomeCourseVO> = persistentListOf(),
    val errorMessage: String? = null,
    /**
     * 이 화면에서 저장한 코스 id.
     *
     * TODO-API-SPEC: 피드 응답(`CourseFeedResponse.Item`)에 저장 여부가 없어 진입 시점의 상태를 알 수 없다.
     * 그래서 "이 화면에서 방금 저장한 것"만 채워진 아이콘으로 보여준다. 서버가 저장 여부를 내려주면
     * 초기값을 채워 정확한 상태로 바꾼다. [wiki-needed]
     */
    val savedCourseIds: ImmutableSet<String> = persistentSetOf(),
    /** 저장 요청이 진행 중인 코스 id(중복 탭 방지). */
    val savingCourseIds: ImmutableSet<String> = persistentSetOf(),
    val actionErrorMessage: String? = null,
) : UiState {
    companion object {
        val empty: HomeFeedUIState = HomeFeedUIState()
    }
}

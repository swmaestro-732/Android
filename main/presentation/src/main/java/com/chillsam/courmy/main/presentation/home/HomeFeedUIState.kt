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
     * 저장한 코스 id. 로드 시 `GET /api/v1/my/saved-courses` 와 대조해 채우고, 저장/취소로 갱신한다.
     *
     * TODO-API-SPEC: 피드 응답(`CourseFeedResponse.Item`)에 저장 여부가 없어 별도 조회로 우회하는 것이다.
     * 서버가 `hasSaved` 를 내려주면 이 집합과 대조 로직을 함께 제거한다. [wiki-needed]
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

package com.chillsam.courmy.main.presentation.home

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.entity.home.HomeCourseVO
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

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
) : UiState {
    companion object {
        val empty: HomeFeedUIState = HomeFeedUIState()
    }
}

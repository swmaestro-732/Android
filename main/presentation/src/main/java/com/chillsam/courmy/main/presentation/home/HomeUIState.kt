package com.chillsam.courmy.main.presentation.home

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.entity.home.HomeCourseVO
import com.chillsam.courmy.main.entity.home.HomeNearbyPlaceVO
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 홈 화면(FS-09) 상태. `GET /service/v2/home` 한 번으로 추천 코스·근처 저장 장소를 함께 받는다.
 *
 * 헤더·하단 탭은 항상 보여야 하므로, 로딩·에러는 화면 전체가 아니라 섹션 자리에서만 분기한다.
 */
data class HomeUIState(
    val isLoading: Boolean = true,
    val courses: ImmutableList<HomeCourseVO> = persistentListOf(),
    /** 근처 저장 장소. 비로그인이거나 근처에 저장한 곳이 없으면 비어 있고, 섹션째 숨긴다. */
    val nearbyPlaces: ImmutableList<HomeNearbyPlaceVO> = persistentListOf(),
    val errorMessage: String? = null,
) : UiState {
    companion object {
        val empty: HomeUIState = HomeUIState()
    }
}

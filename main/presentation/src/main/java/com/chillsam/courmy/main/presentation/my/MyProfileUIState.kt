package com.chillsam.courmy.main.presentation.my

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.entity.my.MyProfileVO
import com.chillsam.courmy.main.entity.profile.ProfileCourseVO
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 마이·프로필(FS-15) 화면 상태. [profile] 이 있으면 정상, 없으면 [isLoading]/[errorMessage] 로 분기.
 *
 * 코스 목록은 페이지를 이어 붙여야 하므로 [profile] 안의 한 페이지가 아니라 [courses] 를 그린다
 * (홈 피드와 같은 방식).
 */
data class MyProfileUIState(
    val isLoading: Boolean = true,
    val profile: MyProfileVO? = null,
    val errorMessage: String? = null,
    val courses: ImmutableList<ProfileCourseVO> = persistentListOf(),
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
    val isLoadingMore: Boolean = false,
) : UiState {
    companion object {
        val empty: MyProfileUIState = MyProfileUIState()
    }
}

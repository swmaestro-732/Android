package com.chillsam.courmy.main.presentation.user

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.entity.profile.ProfileCourseVO
import com.chillsam.courmy.main.entity.user.UserProfileVO
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 타유저 프로필(FS-15) 화면 상태.
 * [profile] 이 있으면 정상, 없으면 [isLoading]/[errorMessage] 로 분기한다.
 * [followErrorMessage] 는 노출 후 [UserProfileIntent.ConsumeFollowError] 로 지우는 1회성 안내다.
 *
 * 코스 목록은 페이지를 이어 붙여야 하므로 [profile] 안의 한 페이지가 아니라 [courses] 를 그린다
 * (홈 피드와 같은 방식).
 */
data class UserProfileUIState(
    val isLoading: Boolean = true,
    val profile: UserProfileVO? = null,
    val errorMessage: String? = null,
    val isFollowInFlight: Boolean = false,
    val followErrorMessage: String? = null,
    /** 비로그인 상태로 팔로우를 눌러 로그인 안내를 띄워야 하는 상태. */
    val needsLogin: Boolean = false,
    val courses: ImmutableList<ProfileCourseVO> = persistentListOf(),
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
    val isLoadingMore: Boolean = false,
) : UiState {
    companion object {
        val empty: UserProfileUIState = UserProfileUIState()
    }
}

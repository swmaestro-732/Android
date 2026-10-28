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
    /**
     * 처음 열었을 때 보여 줄 탭. 계획이 탭 줄에서는 앞이지만 기본값은 코스다 —
     * 계획은 아직 mock 이라, 마이를 열자마자 가짜 목록이 첫 화면이 되면 안 된다.
     */
    val planError: String? = null,
    val selectedTab: ProfileTab = ProfileTab.COURSES,
    /** 계획 목록. 페이징이 없어 [courses] 와 달리 한 번에 다 들고 있는다. */
    val planCourses: ImmutableList<ProfileCourseVO> = persistentListOf(),
) : UiState {
    /** 지금 탭이 그려야 할 목록. 화면이 탭마다 분기하지 않도록 여기서 골라 준다. */
    val visibleCourses: ImmutableList<ProfileCourseVO>
        get() =
            when (selectedTab) {
                ProfileTab.PLAN -> planCourses
                ProfileTab.COURSES -> courses
            }

    companion object {
        val empty: MyProfileUIState = MyProfileUIState()
    }
}

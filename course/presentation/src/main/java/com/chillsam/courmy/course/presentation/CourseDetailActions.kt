package com.chillsam.courmy.course.presentation

/**
 * 코스 상세 화면의 액션 묶음.
 * 콜백이 많아 개별 파라미터로 두면 호출부가 길어지고 순서를 헷갈리기 쉬워 한데 모은다
 * ([com.chillsam.courmy.course.presentation.component.PlaceDetailSheetActions] 선례와 같은 방식).
 */
data class CourseDetailActions(
    val onBack: () -> Unit,
    val onAuthorClick: () -> Unit,
    val onFollowAuthor: () -> Unit,
    val onShare: () -> Unit,
    val onSaveCourse: () -> Unit,
    val onEditCourse: () -> Unit,
    val onDeleteCourse: () -> Unit,
)

/** 상세 페이지가 앱 네비게이션 계층에 위임하는 동작 모음. */
data class CourseDetailPageActions(
    val onBack: () -> Unit,
    val onAuthorClick: (String) -> Unit,
    val onMyProfileClick: () -> Unit,
    val onShare: () -> Unit,
    val onEditCourse: () -> Unit,
    val onLogin: () -> Unit,
)

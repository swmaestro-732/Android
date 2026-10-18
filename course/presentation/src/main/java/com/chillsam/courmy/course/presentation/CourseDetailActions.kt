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
    /** 이 코스를 포크해 코스 만들기로 간다. 내 코스에는 노출하지 않는다. */
    val onForkCourse: () -> Unit,
    /** 하단 "따라가기" — 코스 따라가기 화면(FS-21)으로 간다. */
    val onFollowCourse: () -> Unit,
    val onEditCourse: () -> Unit,
    val onDeleteCourse: () -> Unit,
    val onToggleLike: () -> Unit,
    /** 리뷰 섹션의 "전체보기" → 후기 전체보기 화면(FS-37). */
    val onSeeAllReviews: () -> Unit,
)

/** 상세 페이지가 앱 네비게이션 계층에 위임하는 동작 모음. */
data class CourseDetailPageActions(
    val onBack: () -> Unit,
    val onAuthorClick: (String) -> Unit,
    val onMyProfileClick: () -> Unit,
    val onShare: () -> Unit,
    val onForkCourse: () -> Unit,
    val onFollowCourse: () -> Unit,
    val onEditCourse: () -> Unit,
    val onLogin: () -> Unit,
    val onSeeAllReviews: () -> Unit,
)

package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.MviIntent

/** 후기 작성 화면의 사용자 입력. */
sealed interface CourseReviewWriteIntent : MviIntent {
    data class ChangeRating(
        val rating: Int,
    ) : CourseReviewWriteIntent

    data class ChangeContent(
        val value: String,
    ) : CourseReviewWriteIntent

    /** 시스템 사진 선택 결과. 로컬 URI 목록이다. */
    data class ChangePhotos(
        val uris: List<String>,
    ) : CourseReviewWriteIntent

    /** 태그 칩 토글. 상한을 넘겨 고르려 하면 무시한다. */
    data class ToggleTag(
        val code: String,
    ) : CourseReviewWriteIntent

    /** 제출. 사진 업로드 → 작성 요청 순으로 진행한다. */
    data class Submit(
        val courseId: Long,
    ) : CourseReviewWriteIntent

    /** 에러 안내를 닫았다. */
    data object ConsumeError : CourseReviewWriteIntent

    /**
     * 작성 성공을 호출부가 받아 갔다. 입력을 처음 상태로 되돌린다.
     *
     * 이 ViewModel 은 작성 화면이 아니라 **라우트**에 묶여 있어 화면을 닫아도 살아남는다.
     * 성공 표시를 지우지 않으면 다음에 작성 화면을 열자마자 "이미 썼다"로 판정돼 곧바로 닫힌다.
     */
    data object ConsumeSubmitted : CourseReviewWriteIntent
}

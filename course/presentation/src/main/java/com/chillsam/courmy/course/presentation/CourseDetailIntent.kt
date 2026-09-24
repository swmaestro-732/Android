package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.MviIntent

/**
 * 코스 상세 화면 사용자 입력. View → ViewModel 단일 진입.
 */
sealed interface CourseDetailIntent : MviIntent {
    /** 화면 진입 시 조회할 코스를 지정한다. 같은 코스로 다시 들어오면 재조회하지 않는다. */
    data class Load(
        val courseId: Long,
    ) : CourseDetailIntent

    data object Retry : CourseDetailIntent

    /** 하단 "코스 저장하기" 탭 — 현재 상태의 반대로 요청한다. */
    data object ToggleSave : CourseDetailIntent

    /** 작성자 팔로우/언팔로우. */
    data object ToggleFollowAuthor : CourseDetailIntent

    /** 코스 좋아요 토글. 서버 API가 생기기 전까지 화면 세션 상태로 반영한다. */
    data object ToggleLike : CourseDetailIntent

    /** 댓글 입력값 변경. */
    data class ChangeCommentDraft(
        val value: String,
    ) : CourseDetailIntent

    /** 현재 입력한 댓글 등록. */
    data object SubmitComment : CourseDetailIntent

    /**
     * 하단 "복제" 탭 — 이 코스를 포크해 코스 만들기로 간다(서버 용어는 fork, 화면 문구는 "복제").
     * 화면 이동 전에 로그인 여부만 가린다(포크 저장은 토큰이 필요하다).
     */
    data object ForkCourse : CourseDetailIntent

    /** 포크 화면으로 이동을 끝낸 뒤 신호를 지운다. 남겨 두면 돌아왔을 때 또 이동한다. */
    data object ConsumeForkRequest : CourseDetailIntent

    /** 내 코스 삭제(확인 다이얼로그를 거친 뒤). */
    data object Delete : CourseDetailIntent

    data object ConsumeError : CourseDetailIntent

    /** 로그인 안내를 닫음(로그인하러 가든, 그만두든). */
    data object ConsumeLoginRequired : CourseDetailIntent
}

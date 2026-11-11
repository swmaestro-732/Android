package com.chillsam.courmy.course.entity

/**
 * 코스 댓글 한 건. `GET /api/v1/courses/{courseId}/comments` 응답을 변환한 결과다.
 */
data class CourseCommentVO(
    val id: Long,
    val authorId: Long,
    val content: String,
    /** UTC epoch millis. 화면의 언어로 상대 시각을 표시한다. */
    val createdAtMillis: Long? = null,
    /** 내 댓글 여부. 수정·삭제 메뉴 노출 기준. */
    val isMine: Boolean = false,
    val authorNickname: String = "",
    /** 타인 프로필 조회 키. 누락된 작성자는 프로필 이동을 제공하지 않는다. */
    val authorHandle: String = "",
    val authorProfileImageUrl: String = "",
)

/**
 * 댓글 한 페이지. 서버 응답에는 커서가 따로 없어(items·hasNext 뿐) 마지막 댓글 id 를
 * 다음 요청의 cursor 로 쓴다.
 */
data class CourseCommentPageVO(
    val items: List<CourseCommentVO> = emptyList(),
    val hasNext: Boolean = false,
    val nextCursor: String? = null,
)

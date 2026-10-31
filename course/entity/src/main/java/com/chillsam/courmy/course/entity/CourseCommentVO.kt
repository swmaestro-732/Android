package com.chillsam.courmy.course.entity

/**
 * 코스 댓글 한 건. `GET /api/v1/courses/{courseId}/comments` 응답을 변환한 결과다.
 *
 * 응답에 작성자 이름·아바타가 없어(authorId 뿐) 작성자 표기는 "작성자 N" 형태로 화면이 만든다.
 * TODO-API-SPEC: 서버가 작성자 프로필을 내려주면 VO 에 싣고 표기를 교체한다. [wiki-needed]
 */
data class CourseCommentVO(
    val id: Long,
    val authorId: Long,
    val content: String,
    /** 상대 시각 표기(예: "2일 전"). */
    val dateText: String = "",
    /** 내 댓글 여부. 수정·삭제 메뉴 노출 기준. */
    val isMine: Boolean = false,
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

package com.chillsam.courmy.course.data.courseComments.dto

import com.chillsam.courmy.common.entity.time.toRelativeTimeText
import com.chillsam.courmy.course.entity.CourseCommentPageVO
import com.chillsam.courmy.course.entity.CourseCommentVO
import kotlinx.serialization.Serializable

/**
 * 코스 댓글 API DTO(`/api/v1/courses/{courseId}/comments`). 봉투는 공통 계약 `{ code, message, data }`.
 *
 * 응답에는 `updatedAt` 도 오지만 화면이 쓰지 않아 매핑하지 않는다.
 * `isMine` 은 실호출로 필드명 확인(2026-10-19) — springdoc 의 is-접두 제거 오표기가 아니다.
 */
@Serializable
data class CourseCommentListEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: CourseCommentListDTO? = null,
)

@Serializable
data class CourseCommentListDTO(
    val items: List<CourseCommentItemDTO>? = null,
    val hasNext: Boolean = false,
)

@Serializable
data class CourseCommentItemDTO(
    val id: Long? = null,
    val authorId: Long? = null,
    val content: String? = null,
    val createdAt: String? = null,
    val isMine: Boolean = false,
)

@Serializable
data class WriteCourseCommentRequest(
    val content: String,
)

@Serializable
data class CourseCommentIdEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: CourseCommentIdDTO? = null,
)

@Serializable
data class CourseCommentIdDTO(
    val commentId: Long? = null,
)

/** data 없는 공통 응답(수정·삭제). */
@Serializable
data class CourseCommentActionEnvelope(
    val code: Int? = null,
    val message: String? = null,
)

fun CourseCommentListDTO.toVO(): CourseCommentPageVO {
    val comments =
        items
            .orEmpty()
            // id 가 없으면 수정·삭제 대상을 특정할 수 없어 버린다.
            .filter { it.id != null }
            .map { item ->
                CourseCommentVO(
                    id = requireNotNull(item.id),
                    authorId = item.authorId ?: 0L,
                    content = item.content.orEmpty(),
                    dateText = item.createdAt.toRelativeTimeText(),
                    isMine = item.isMine,
                )
            }
    return CourseCommentPageVO(
        items = comments,
        // 응답에 커서가 없어 마지막 댓글 id 를 다음 cursor 로 쓴다. 더 없으면 null.
        hasNext = hasNext && comments.isNotEmpty(),
        nextCursor =
            comments
                .lastOrNull()
                ?.id
                ?.toString()
                .takeIf { hasNext },
    )
}

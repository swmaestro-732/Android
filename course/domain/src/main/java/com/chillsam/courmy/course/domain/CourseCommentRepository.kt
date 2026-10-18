package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseCommentPageVO

/** 코스 댓글 Repository(`/api/v1/courses/{courseId}/comments`). 구현은 실패 시 예외를 throw 한다. */
interface CourseCommentRepository {
    suspend fun getComments(
        courseId: Long,
        cursor: String? = null,
        size: Int = DEFAULT_PAGE_SIZE,
    ): CourseCommentPageVO

    /** 댓글 작성. 생성된 commentId 를 돌려준다. */
    suspend fun writeComment(
        courseId: Long,
        content: String,
    ): Long

    suspend fun editComment(
        courseId: Long,
        commentId: Long,
        content: String,
    )

    suspend fun deleteComment(
        courseId: Long,
        commentId: Long,
    )

    companion object {
        const val DEFAULT_PAGE_SIZE: Int = 20
    }
}

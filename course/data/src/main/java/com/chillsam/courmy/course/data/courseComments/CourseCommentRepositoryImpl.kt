package com.chillsam.courmy.course.data.courseComments

import com.chillsam.courmy.course.data.courseComments.dto.WriteCourseCommentRequest
import com.chillsam.courmy.course.data.courseComments.dto.toVO
import com.chillsam.courmy.course.domain.CourseCommentRepository
import com.chillsam.courmy.course.entity.CourseCommentPageVO

class CourseCommentRepositoryImpl(
    private val dataSource: CourseCommentDataSource,
) : CourseCommentRepository {
    override suspend fun getComments(
        courseId: Long,
        cursor: String?,
        size: Int,
    ): CourseCommentPageVO {
        val envelope = dataSource.getComments(courseId, cursor, size)
        val data = requireNotNull(envelope.data) { "댓글 목록 응답에 data 가 없습니다." }
        return data.toVO()
    }

    override suspend fun writeComment(
        courseId: Long,
        content: String,
    ): Long {
        val envelope = dataSource.writeComment(courseId, WriteCourseCommentRequest(content))
        return requireNotNull(envelope.data?.commentId) { "댓글 작성 응답에 commentId 가 없습니다." }
    }

    override suspend fun editComment(
        courseId: Long,
        commentId: Long,
        content: String,
    ) {
        dataSource.editComment(courseId, commentId, WriteCourseCommentRequest(content))
    }

    override suspend fun deleteComment(
        courseId: Long,
        commentId: Long,
    ) {
        dataSource.deleteComment(courseId, commentId)
    }
}

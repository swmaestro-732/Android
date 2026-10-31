package com.chillsam.courmy.course.data.courseComments

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.course.data.courseComments.dto.CourseCommentActionEnvelope
import com.chillsam.courmy.course.data.courseComments.dto.CourseCommentIdEnvelope
import com.chillsam.courmy.course.data.courseComments.dto.CourseCommentListEnvelope
import com.chillsam.courmy.course.data.courseComments.dto.WriteCourseCommentRequest

class CourseCommentDataSource(
    private val apiService: CourseCommentApiService,
) : BaseRemoteDataSource() {
    suspend fun getComments(
        courseId: Long,
        cursor: String?,
        size: Int,
    ): CourseCommentListEnvelope = checkResponse(apiService.getComments(courseId, cursor, size))

    /** 쓰기(작성·수정·삭제)는 폴백 없이 예외로 올린다 — 가짜 성공을 만들지 않는다. */
    suspend fun writeComment(
        courseId: Long,
        request: WriteCourseCommentRequest,
    ): CourseCommentIdEnvelope = checkResponse(apiService.writeComment(courseId, request))

    suspend fun editComment(
        courseId: Long,
        commentId: Long,
        request: WriteCourseCommentRequest,
    ): CourseCommentActionEnvelope = checkResponse(apiService.editComment(courseId, commentId, request))

    suspend fun deleteComment(
        courseId: Long,
        commentId: Long,
    ): CourseCommentActionEnvelope = checkResponse(apiService.deleteComment(courseId, commentId))
}

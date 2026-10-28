package com.chillsam.courmy.course.data.courseComments

import com.chillsam.courmy.course.data.courseComments.dto.CourseCommentActionEnvelope
import com.chillsam.courmy.course.data.courseComments.dto.CourseCommentIdEnvelope
import com.chillsam.courmy.course.data.courseComments.dto.CourseCommentListEnvelope
import com.chillsam.courmy.course.data.courseComments.dto.WriteCourseCommentRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/** 스펙의 `userId`·`viewerId` 쿼리는 springdoc 오표기라 보내지 않는다(JWT 가 정본). */
interface CourseCommentApiService {
    @GET("api/v1/courses/{courseId}/comments")
    suspend fun getComments(
        @Path("courseId") courseId: Long,
        @Query("cursor") cursor: String?,
        @Query("size") size: Int,
    ): Response<CourseCommentListEnvelope>

    @POST("api/v1/courses/{courseId}/comments")
    suspend fun writeComment(
        @Path("courseId") courseId: Long,
        @Body request: WriteCourseCommentRequest,
    ): Response<CourseCommentIdEnvelope>

    @PATCH("api/v1/courses/{courseId}/comments/{commentId}")
    suspend fun editComment(
        @Path("courseId") courseId: Long,
        @Path("commentId") commentId: Long,
        @Body request: WriteCourseCommentRequest,
    ): Response<CourseCommentActionEnvelope>

    @DELETE("api/v1/courses/{courseId}/comments/{commentId}")
    suspend fun deleteComment(
        @Path("courseId") courseId: Long,
        @Path("commentId") commentId: Long,
    ): Response<CourseCommentActionEnvelope>
}

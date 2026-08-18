package com.chillsam.courmy.course.data.courseManage

import com.chillsam.courmy.course.data.courseManage.dto.CourseSourceEnvelope
import com.chillsam.courmy.course.data.courseManage.dto.UpdateCourseRequest
import com.chillsam.courmy.course.data.courseSave.dto.CourseSaveEnvelope
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

/** 내 코스 관리(편집·삭제). 작성자 본인만 호출할 수 있고 서버가 JWT 로 판정한다. */
interface CourseManageApiService {
    /** 코스 원본. 편집 화면이 공개 설정을 읽으려고만 호출한다(화면 조합 API 에는 그 필드가 없다). */
    @GET("api/v1/courses/{courseId}")
    suspend fun getCourse(
        @Path("courseId") courseId: Long,
    ): Response<CourseSourceEnvelope>

    @PATCH("api/v1/courses/{courseId}")
    suspend fun updateCourse(
        @Path("courseId") courseId: Long,
        @Body request: UpdateCourseRequest,
    ): Response<CourseSaveEnvelope>

    @DELETE("api/v1/courses/{courseId}")
    suspend fun deleteCourse(
        @Path("courseId") courseId: Long,
    ): Response<CourseSaveEnvelope>
}

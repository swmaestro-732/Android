package com.chillsam.courmy.course.data.courseManage

import com.chillsam.courmy.course.data.courseSave.dto.CourseSaveEnvelope
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Path

/**
 * 내 코스 관리(삭제). 작성자 본인만 호출할 수 있고 서버가 JWT 로 판정한다.
 *
 * TODO-API-SPEC: 편집(`PATCH /api/v1/courses/{courseId}`)은 편집 화면이 생기면 여기에 추가한다. [wiki-needed]
 */
interface CourseManageApiService {
    @DELETE("api/v1/courses/{courseId}")
    suspend fun deleteCourse(
        @Path("courseId") courseId: Long,
    ): Response<CourseSaveEnvelope>
}

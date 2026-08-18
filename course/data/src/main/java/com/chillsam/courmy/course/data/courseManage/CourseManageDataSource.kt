package com.chillsam.courmy.course.data.courseManage

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.course.data.courseManage.dto.UpdateCourseRequest
import com.chillsam.courmy.course.data.courseManage.dto.toVisibilityOrNull
import com.chillsam.courmy.course.entity.CourseVisibility

class CourseManageDataSource(
    private val apiService: CourseManageApiService,
) : BaseRemoteDataSource() {
    /** 공개 설정만 읽는다. 응답에 없거나 모르는 값이면 null 을 돌려 호출부가 "알 수 없음"으로 다룬다. */
    suspend fun getVisibility(courseId: Long): CourseVisibility? =
        checkResponse(apiService.getCourse(courseId)).toVisibilityOrNull()

    suspend fun updateCourse(
        courseId: Long,
        request: UpdateCourseRequest,
    ) {
        // 편집 성공도 본문이 없을 수 있어 상태 코드만 확인한다.
        checkSuccess(apiService.updateCourse(courseId, request))
    }

    suspend fun deleteCourse(courseId: Long) {
        // 삭제 성공은 본문이 없을 수 있어 상태 코드만 확인한다.
        checkSuccess(apiService.deleteCourse(courseId))
    }
}

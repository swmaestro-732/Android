package com.chillsam.courmy.course.data.courseSave

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.course.data.courseSave.dto.SaveCourseRequest

class CourseSaveDataSource(
    private val apiService: CourseSaveApiService,
) : BaseRemoteDataSource() {
    suspend fun save(courseId: Long) {
        // 저장은 201(Created)이고 응답 바디가 비어 있을 수 있어 상태 코드만 확인한다.
        checkSuccess(apiService.save(SaveCourseRequest(courseId = courseId)))
    }

    suspend fun unsave(courseId: Long) {
        checkSuccess(apiService.unsave(courseId))
    }
}

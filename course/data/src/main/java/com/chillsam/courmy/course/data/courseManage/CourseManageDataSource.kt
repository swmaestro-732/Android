package com.chillsam.courmy.course.data.courseManage

import com.chillsam.courmy.common.data.BaseRemoteDataSource

class CourseManageDataSource(
    private val apiService: CourseManageApiService,
) : BaseRemoteDataSource() {
    suspend fun deleteCourse(courseId: Long) {
        // 삭제 성공은 본문이 없을 수 있어 상태 코드만 확인한다.
        checkSuccess(apiService.deleteCourse(courseId))
    }
}

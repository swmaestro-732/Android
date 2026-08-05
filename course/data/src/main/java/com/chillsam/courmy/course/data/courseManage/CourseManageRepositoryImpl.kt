package com.chillsam.courmy.course.data.courseManage

import com.chillsam.courmy.course.domain.CourseManageRepository

class CourseManageRepositoryImpl(
    private val dataSource: CourseManageDataSource,
) : CourseManageRepository {
    override suspend fun deleteCourse(courseId: Long) = dataSource.deleteCourse(courseId)
}

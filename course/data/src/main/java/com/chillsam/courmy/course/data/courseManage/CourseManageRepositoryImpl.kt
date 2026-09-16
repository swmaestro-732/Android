package com.chillsam.courmy.course.data.courseManage

import com.chillsam.courmy.course.data.courseManage.dto.toUpdateRequest
import com.chillsam.courmy.course.domain.CourseManageRepository
import com.chillsam.courmy.course.entity.CourseEditVO
import com.chillsam.courmy.course.entity.CourseVisibility

class CourseManageRepositoryImpl(
    private val dataSource: CourseManageDataSource,
) : CourseManageRepository {
    override suspend fun getVisibility(courseId: Long): CourseVisibility? = dataSource.getVisibility(courseId)

    override suspend fun updateCourse(
        courseId: Long,
        edit: CourseEditVO,
    ) = dataSource.updateCourse(courseId, edit.toUpdateRequest())

    override suspend fun deleteCourse(courseId: Long) = dataSource.deleteCourse(courseId)
}

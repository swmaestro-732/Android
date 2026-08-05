package com.chillsam.courmy.course.data.courseSave

import com.chillsam.courmy.course.domain.CourseSaveRepository

class CourseSaveRepositoryImpl(
    private val dataSource: CourseSaveDataSource,
) : CourseSaveRepository {
    override suspend fun setSaved(
        courseId: Long,
        saved: Boolean,
    ) {
        if (saved) dataSource.save(courseId) else dataSource.unsave(courseId)
    }
}

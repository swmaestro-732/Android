package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.telemetry.AppFlow
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.chillsam.courmy.common.domain.telemetry.track
import com.chillsam.courmy.course.entity.CourseEditVO
import javax.inject.Inject

/**
 * 내 코스 편집. 코스 정보·태그·장소별 한마디만 바뀌며, 장소 구성과 이미지는 불러온 값을 그대로 되돌려 보낸다
 * ([CourseEditVO] 주석 참고).
 */
class UpdateCourseUseCase
    @Inject
    constructor(
        private val repository: CourseManageRepository,
        private val telemetry: Telemetry,
    ) {
        suspend operator fun invoke(
            courseId: Long,
            edit: CourseEditVO,
        ) = telemetry.track(AppFlow.CourseUpdate) { repository.updateCourse(courseId, edit) }
    }

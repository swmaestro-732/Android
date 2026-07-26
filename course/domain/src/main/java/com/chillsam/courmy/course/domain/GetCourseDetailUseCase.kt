package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseDetailVO
import javax.inject.Inject

/**
 * 코스 상세 조회 UseCase(FS-11).
 * 현재는 단순 pass-through. 실패 처리(재시도/에러 화면)가 필요해지면 여기서 도메인 에러로 변환한다.
 */
class GetCourseDetailUseCase
    @Inject
    constructor(
        private val repository: CourseRepository,
    ) {
        suspend operator fun invoke(courseId: Long): CourseDetailVO = repository.getCourseDetail(courseId)
    }

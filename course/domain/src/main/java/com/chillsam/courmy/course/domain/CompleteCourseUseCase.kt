package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.telemetry.AppFlow
import com.chillsam.courmy.common.domain.telemetry.FlowResult
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.chillsam.courmy.course.entity.CourseCompleteVO
import javax.inject.Inject

/** 코스 저장을 완료한다(완성 화면 데이터 보관 + 저장 목록 추가). */
class CompleteCourseUseCase
    @Inject
    constructor(
        private val repository: CourseRepository,
        private val telemetry: Telemetry,
    ) {
        operator fun invoke(course: CourseCompleteVO?) {
            repository.completeCourse(course)
            // 로컬 처리라 실패할 일이 없다. 생성 → 완성까지 실제로 도달했는지 보기 위한 퍼널 신호로만 남긴다.
            telemetry.logFlow(AppFlow.CourseComplete, FlowResult.Success)
        }
    }

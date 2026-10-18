package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.telemetry.AppFlow
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.chillsam.courmy.common.domain.telemetry.track
import com.chillsam.courmy.course.entity.CourseLikeVO
import javax.inject.Inject

/**
 * 코스 좋아요/취소 토글 UseCase.
 * 저장 토글([SetCourseSavedUseCase])과 같은 결 — 실패는 예외 그대로 올려 화면이 되돌리게 한다.
 */
class SetCourseLikedUseCase
    @Inject
    constructor(
        private val repository: CourseLikeRepository,
        private val telemetry: Telemetry,
    ) {
        suspend operator fun invoke(
            courseId: Long,
            liked: Boolean,
        ): CourseLikeVO? = telemetry.track(AppFlow.CourseLike) { repository.setLiked(courseId, liked) }
    }

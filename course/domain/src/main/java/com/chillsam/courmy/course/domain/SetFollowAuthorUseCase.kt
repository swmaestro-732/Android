package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.telemetry.AppFlow
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.chillsam.courmy.common.domain.telemetry.track
import javax.inject.Inject

/** 코스 작성자를 팔로우/언팔로우하고, 반영된 상태를 돌려준다. */
class SetFollowAuthorUseCase
    @Inject
    constructor(
        private val repository: CourseRepository,
        private val telemetry: Telemetry,
    ) {
        suspend operator fun invoke(
            userId: Long,
            follow: Boolean,
        ): Boolean = telemetry.track(AppFlow.FollowAuthor) { repository.setFollowAuthor(userId, follow) }
    }

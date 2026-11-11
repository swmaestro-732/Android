package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.error.LocalizedValidationException
import com.chillsam.courmy.common.domain.error.requireLocalized
import com.chillsam.courmy.common.domain.helper.StringResource
import com.chillsam.courmy.common.domain.telemetry.AppFlow
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.chillsam.courmy.common.domain.telemetry.track
import javax.inject.Inject

/** 코스 댓글 작성 UseCase. 서버 제한(1000자)을 요청 전에 거른다. */
class WriteCourseCommentUseCase
    @Inject
    constructor(
        private val repository: CourseCommentRepository,
        private val telemetry: Telemetry,
    ) {
        suspend operator fun invoke(
            courseId: Long,
            content: String,
        ): Long {
            requireLocalized(courseId > 0) { LocalizedValidationException(StringResource.VALIDATION_COURSE_TARGET) }
            val trimmed = content.trim()
            requireLocalized(
                trimmed.isNotEmpty(),
            ) { LocalizedValidationException(StringResource.VALIDATION_COMMENT_REQUIRED) }
            requireLocalized(trimmed.length <= MAX_LENGTH) {
                LocalizedValidationException(StringResource.VALIDATION_COMMENT_LENGTH, MAX_LENGTH)
            }
            return telemetry.track(AppFlow.CommentWrite) { repository.writeComment(courseId, trimmed) }
        }

        companion object {
            const val MAX_LENGTH = 1000
        }
    }

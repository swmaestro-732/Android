package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.error.LocalizedValidationException
import com.chillsam.courmy.common.domain.error.requireLocalized
import com.chillsam.courmy.common.domain.helper.StringResource
import com.chillsam.courmy.course.entity.CourseCommentPageVO
import javax.inject.Inject

/** 코스 댓글 한 페이지 조회 UseCase. */
class GetCourseCommentsUseCase
    @Inject
    constructor(
        private val repository: CourseCommentRepository,
    ) {
        suspend operator fun invoke(
            courseId: Long,
            cursor: String? = null,
        ): CourseCommentPageVO {
            requireLocalized(courseId > 0) { LocalizedValidationException(StringResource.VALIDATION_COURSE_TARGET) }
            return repository.getComments(courseId, cursor)
        }
    }

package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.error.LocalizedValidationException
import com.chillsam.courmy.common.domain.error.requireLocalized
import com.chillsam.courmy.common.domain.helper.StringResource
import javax.inject.Inject

/** 코스 댓글 삭제 UseCase. */
class DeleteCourseCommentUseCase
    @Inject
    constructor(
        private val repository: CourseCommentRepository,
    ) {
        suspend operator fun invoke(
            courseId: Long,
            commentId: Long,
        ) {
            requireLocalized(courseId > 0 && commentId > 0) {
                LocalizedValidationException(StringResource.VALIDATION_COMMENT_TARGET)
            }
            repository.deleteComment(courseId, commentId)
        }
    }

package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.error.LocalizedValidationException
import com.chillsam.courmy.common.domain.error.requireLocalized
import com.chillsam.courmy.common.domain.helper.StringResource
import javax.inject.Inject

/** 코스 댓글 수정 UseCase. 작성과 같은 검증을 거친다. */
class EditCourseCommentUseCase
    @Inject
    constructor(
        private val repository: CourseCommentRepository,
    ) {
        suspend operator fun invoke(
            courseId: Long,
            commentId: Long,
            content: String,
        ) {
            requireLocalized(courseId > 0 && commentId > 0) {
                LocalizedValidationException(StringResource.VALIDATION_COMMENT_TARGET)
            }
            val trimmed = content.trim()
            requireLocalized(
                trimmed.isNotEmpty(),
            ) { LocalizedValidationException(StringResource.VALIDATION_COMMENT_REQUIRED) }
            requireLocalized(trimmed.length <= WriteCourseCommentUseCase.MAX_LENGTH) {
                LocalizedValidationException(
                    StringResource.VALIDATION_COMMENT_LENGTH,
                    WriteCourseCommentUseCase.MAX_LENGTH,
                )
            }
            repository.editComment(courseId, commentId, trimmed)
        }
    }

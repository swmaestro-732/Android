package com.chillsam.courmy.course.domain

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
            require(courseId > 0 && commentId > 0) { "댓글을 확인해 주세요." }
            val trimmed = content.trim()
            require(trimmed.isNotEmpty()) { "댓글 내용을 입력해 주세요." }
            require(trimmed.length <= WriteCourseCommentUseCase.MAX_LENGTH) {
                "댓글은 ${WriteCourseCommentUseCase.MAX_LENGTH}자까지 쓸 수 있어요."
            }
            repository.editComment(courseId, commentId, trimmed)
        }
    }

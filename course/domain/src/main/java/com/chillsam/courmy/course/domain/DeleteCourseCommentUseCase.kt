package com.chillsam.courmy.course.domain

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
            require(courseId > 0 && commentId > 0) { "댓글을 확인해 주세요." }
            repository.deleteComment(courseId, commentId)
        }
    }

package com.chillsam.courmy.course.domain

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
            require(courseId > 0) { "코스를 확인해 주세요." }
            return repository.getComments(courseId, cursor)
        }
    }

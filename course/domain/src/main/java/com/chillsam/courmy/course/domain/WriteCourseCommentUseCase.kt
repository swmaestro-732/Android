package com.chillsam.courmy.course.domain

import javax.inject.Inject

/** 코스 댓글 작성 UseCase. 서버 제한(1000자)을 요청 전에 거른다. */
class WriteCourseCommentUseCase
    @Inject
    constructor(
        private val repository: CourseCommentRepository,
    ) {
        suspend operator fun invoke(
            courseId: Long,
            content: String,
        ): Long {
            require(courseId > 0) { "코스를 확인해 주세요." }
            val trimmed = content.trim()
            require(trimmed.isNotEmpty()) { "댓글 내용을 입력해 주세요." }
            require(trimmed.length <= MAX_LENGTH) { "댓글은 ${MAX_LENGTH}자까지 쓸 수 있어요." }
            return repository.writeComment(courseId, trimmed)
        }

        companion object {
            const val MAX_LENGTH = 1000
        }
    }

package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.error.LocalizedValidationException
import com.chillsam.courmy.common.domain.error.requireLocalized
import com.chillsam.courmy.common.domain.helper.StringResource
import javax.inject.Inject

/**
 * 후기 삭제 UseCase.
 *
 * 서버는 (내 id, courseId, reviewId) 가 모두 맞을 때만 지우고, 남의 리뷰나 이미 지운 리뷰는
 * 사유를 숨겨 404 로 답한다. 그래서 "권한 없음" 과 "없는 리뷰" 를 앱에서 구분할 수 없다.
 */
class DeleteCourseReviewUseCase
    @Inject
    constructor(
        private val repository: CourseReviewRepository,
    ) {
        suspend operator fun invoke(
            courseId: Long,
            reviewId: Long,
        ) {
            requireLocalized(reviewId > 0) { LocalizedValidationException(StringResource.VALIDATION_REVIEW_DELETE) }
            repository.deleteCourseReview(courseId, reviewId)
        }
    }

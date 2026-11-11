package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.error.LocalizedValidationException
import com.chillsam.courmy.common.domain.error.requireLocalized
import com.chillsam.courmy.common.domain.helper.StringResource
import javax.inject.Inject

/**
 * 장소 후기 삭제 UseCase(`DELETE /api/v1/places/{placeId}/reviews/{reviewId}`).
 *
 * 코스 후기([DeleteCourseReviewUseCase])와 같은 규칙이다 — 서버는 (내 id, placeId, reviewId) 가
 * 모두 맞을 때만 지우고, 남의 리뷰·이미 지운 리뷰는 사유를 숨겨 404 로 답한다.
 */
class DeletePlaceReviewUseCase
    @Inject
    constructor(
        private val repository: PlaceReviewRepository,
    ) {
        suspend operator fun invoke(
            placeId: Long,
            reviewId: Long,
        ) {
            requireLocalized(
                placeId > 0,
            ) { LocalizedValidationException(StringResource.VALIDATION_REVIEW_DELETE_PLACE) }
            requireLocalized(reviewId > 0) { LocalizedValidationException(StringResource.VALIDATION_REVIEW_DELETE) }
            repository.deletePlaceReview(placeId, reviewId)
        }
    }

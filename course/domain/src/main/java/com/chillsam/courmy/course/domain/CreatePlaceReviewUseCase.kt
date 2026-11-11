package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.error.LocalizedValidationException
import com.chillsam.courmy.common.domain.error.requireLocalized
import com.chillsam.courmy.common.domain.helper.StringResource
import com.chillsam.courmy.common.domain.media.MediaRepository
import com.chillsam.courmy.common.domain.media.UploadPurpose
import com.chillsam.courmy.common.domain.telemetry.AppFlow
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.chillsam.courmy.common.domain.telemetry.track
import com.chillsam.courmy.course.entity.CourseReviewDraftVO
import com.chillsam.courmy.course.entity.CourseReviewLimits
import javax.inject.Inject

/**
 * 장소 후기 작성 UseCase(`POST /api/v1/places/{placeId}/reviews`).
 *
 * [CreateCourseReviewUseCase] 와 같은 절차다 — 사진은 로컬 URI 로 들어오므로 먼저 업로드해
 * 공개 URL 로 바꾼 뒤 싣고, 업로드 실패는 삼키지 않고 예외로 올린다(장소도 1인 1건이라
 * 사진이 빠진 채 등록되면 고칠 수 없다 — 재작성은 서버가 409 로 막는다).
 * 입력 제약(별점 1~5, 본문 1000자, 사진 6장, 태그 5개)도 서버 계약이 같아 그대로 쓴다.
 *
 * 장소 후기 작성의 데이터 계약이며 현재 화면에는 작성 진입점이 없다.
 */
class CreatePlaceReviewUseCase
    @Inject
    constructor(
        private val repository: PlaceReviewRepository,
        private val mediaRepository: MediaRepository,
        private val telemetry: Telemetry,
    ) {
        /** 작성된 리뷰 id 를 돌려준다. */
        suspend operator fun invoke(
            placeId: Long,
            draft: CourseReviewDraftVO,
        ): Long {
            requireLocalized(placeId > 0) { LocalizedValidationException(StringResource.VALIDATION_REVIEW_PLACE) }
            requireLocalized(draft.rating in CourseReviewLimits.MIN_RATING..CourseReviewLimits.MAX_RATING) {
                LocalizedValidationException(
                    StringResource.VALIDATION_RATING_RANGE,
                    CourseReviewLimits.MIN_RATING,
                    CourseReviewLimits.MAX_RATING,
                    draft.rating,
                )
            }
            requireLocalized(draft.content.orEmpty().length <= CourseReviewLimits.MAX_CONTENT_LENGTH) {
                LocalizedValidationException(
                    StringResource.VALIDATION_REVIEW_LENGTH,
                    CourseReviewLimits.MAX_CONTENT_LENGTH,
                )
            }
            requireLocalized(draft.photoUrls.size <= CourseReviewLimits.MAX_PHOTOS) {
                LocalizedValidationException(StringResource.VALIDATION_REVIEW_PHOTOS, CourseReviewLimits.MAX_PHOTOS)
            }
            requireLocalized(draft.tagCodes.size <= CourseReviewLimits.MAX_TAGS) {
                LocalizedValidationException(StringResource.VALIDATION_REVIEW_TAGS, CourseReviewLimits.MAX_TAGS)
            }

            return telemetry.track(AppFlow.ReviewWrite) {
                val uploaded =
                    draft.photoUrls.map { uri ->
                        // 이미 올라간 사진(재시도 등)은 그대로 쓴다.
                        if (uri.startsWith("http")) uri else mediaRepository.uploadImage(uri, UploadPurpose.PLACE)
                    }
                repository.createPlaceReview(placeId, draft.copy(photoUrls = uploaded))
            }
        }
    }

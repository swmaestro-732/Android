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
 * 후기 작성 UseCase.
 *
 * 사진은 로컬 URI(`content://`)로 들어오므로 **먼저 업로드해 공개 URL 로 바꾼 뒤** 작성 요청에 싣는다.
 * 로컬 URI 를 그대로 보내면 저장은 되지만 어디서도 보이지 않는 사진이 된다.
 *
 * 코스 저장([CreateCourseUseCase])과 달리 업로드 실패를 **삼키지 않고 예외로 올린다**.
 * 후기는 코스당 1건만 쓸 수 있어서, 사진이 빠진 채로 등록되면 고쳐 쓸 방법이 없기 때문이다
 * (재작성은 서버가 409 로 막는다).
 */
class CreateCourseReviewUseCase
    @Inject
    constructor(
        private val repository: CourseReviewRepository,
        private val mediaRepository: MediaRepository,
        private val telemetry: Telemetry,
    ) {
        /** 작성된 리뷰 id 를 돌려준다. */
        suspend operator fun invoke(
            courseId: Long,
            draft: CourseReviewDraftVO,
        ): Long {
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
                        if (uri.startsWith("http")) uri else mediaRepository.uploadImage(uri, UploadPurpose.COURSE)
                    }
                repository.createCourseReview(courseId, draft.copy(photoUrls = uploaded))
            }
        }
    }

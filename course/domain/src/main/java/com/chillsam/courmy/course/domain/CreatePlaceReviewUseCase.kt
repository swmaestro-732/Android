package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.media.MediaRepository
import com.chillsam.courmy.common.domain.media.UploadPurpose
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
 * TODO: 장소 후기 작성 UI 는 아직 없다. 장소 시트에 작성 진입점이 생기면 연결한다.
 */
class CreatePlaceReviewUseCase
    @Inject
    constructor(
        private val repository: PlaceReviewRepository,
        private val mediaRepository: MediaRepository,
    ) {
        /** 작성된 리뷰 id 를 돌려준다. */
        suspend operator fun invoke(
            placeId: Long,
            draft: CourseReviewDraftVO,
        ): Long {
            require(placeId > 0) { "후기를 남길 장소 id 가 없습니다." }
            require(draft.rating in CourseReviewLimits.MIN_RATING..CourseReviewLimits.MAX_RATING) {
                "별점은 ${CourseReviewLimits.MIN_RATING}~${CourseReviewLimits.MAX_RATING} 만 보낼 수 있습니다: ${draft.rating}"
            }
            require(draft.content.orEmpty().length <= CourseReviewLimits.MAX_CONTENT_LENGTH) {
                "후기는 ${CourseReviewLimits.MAX_CONTENT_LENGTH}자까지 쓸 수 있습니다."
            }
            require(draft.photoUrls.size <= CourseReviewLimits.MAX_PHOTOS) {
                "사진은 ${CourseReviewLimits.MAX_PHOTOS}장까지 올릴 수 있습니다."
            }
            require(draft.tagCodes.size <= CourseReviewLimits.MAX_TAGS) {
                "태그는 ${CourseReviewLimits.MAX_TAGS}개까지 고를 수 있습니다."
            }

            val uploaded =
                draft.photoUrls.map { uri ->
                    // 이미 올라간 사진(재시도 등)은 그대로 쓴다.
                    if (uri.startsWith("http")) uri else mediaRepository.uploadImage(uri, UploadPurpose.PLACE)
                }
            return repository.createPlaceReview(placeId, draft.copy(photoUrls = uploaded))
        }
    }

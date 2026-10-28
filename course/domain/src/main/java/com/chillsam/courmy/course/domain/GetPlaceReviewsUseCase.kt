package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseReviewListVO
import com.chillsam.courmy.course.entity.CourseReviewSort
import javax.inject.Inject

/**
 * 장소 후기 조회 UseCase(FS-11 장소 상세 시트).
 *
 * 정렬·페이징은 서버가 하므로 여기서 목록을 다시 손대지 않는다. [GetCourseReviewsUseCase] 와 같은
 * pass-through 이며, 실패 처리가 필요해지면 여기서 도메인 에러로 바꾼다.
 */
class GetPlaceReviewsUseCase
    @Inject
    constructor(
        private val repository: PlaceReviewRepository,
    ) {
        suspend operator fun invoke(
            placeId: Long,
            sort: CourseReviewSort = CourseReviewSort.LATEST,
            cursor: String? = null,
            size: Int = PlaceReviewRepository.DEFAULT_PAGE_SIZE,
        ): CourseReviewListVO = repository.getPlaceReviews(placeId, sort, cursor, size)
    }

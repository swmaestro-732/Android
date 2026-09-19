package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseReviewListVO
import com.chillsam.courmy.course.entity.CourseReviewSort
import javax.inject.Inject

/**
 * 코스 후기 전체보기 조회 UseCase(FS-37).
 *
 * 정렬·페이징은 서버가 하므로 여기서 목록을 다시 손대지 않는다. [GetCourseDetailUseCase] 와 같은
 * pass-through 이며, 실패 처리가 필요해지면 여기서 도메인 에러로 바꾼다.
 */
class GetCourseReviewsUseCase
    @Inject
    constructor(
        private val repository: CourseReviewRepository,
    ) {
        suspend operator fun invoke(
            courseId: Long,
            sort: CourseReviewSort = CourseReviewSort.LATEST,
            cursor: String? = null,
            size: Int = CourseReviewRepository.DEFAULT_PAGE_SIZE,
        ): CourseReviewListVO = repository.getCourseReviews(courseId, sort, cursor, size)
    }

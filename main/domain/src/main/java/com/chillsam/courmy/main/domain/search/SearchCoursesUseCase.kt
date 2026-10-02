package com.chillsam.courmy.main.domain.search

import com.chillsam.courmy.main.entity.search.SearchCourseVO
import javax.inject.Inject

/**
 * 코스 검색 UseCase.
 *
 * 서버가 `size` 1~50 밖을 400 으로 거부하므로 호출부가 임의의 값을 넣더라도 여기서 범위 안으로 맞춘다
 * (홈 피드 [com.chillsam.courmy.main.domain.home.GetHomeFeedUseCase] 와 같은 이유).
 */
class SearchCoursesUseCase
    @Inject
    constructor(
        private val repository: CourseSearchRepository,
    ) {
        suspend operator fun invoke(
            keyword: String,
            size: Int = DEFAULT_SIZE,
        ): List<SearchCourseVO> = repository.searchCourses(keyword, size.coerceIn(MIN_SIZE, MAX_SIZE))

        companion object {
            const val DEFAULT_SIZE = 20
            private const val MIN_SIZE = 1
            private const val MAX_SIZE = 50
        }
    }

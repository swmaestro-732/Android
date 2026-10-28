package com.chillsam.courmy.course.domain

import javax.inject.Inject

/**
 * 코스에 담은 장소들로 추천 태그를 받는다(`GET /api/v1/recommended-tags`).
 *
 * 태그는 코스 저장의 필수 조건이 아니므로, 실패해도 화면을 막지 않고 호출부가 빈 목록으로 넘어간다.
 */
class GetRecommendedTagsUseCase
    @Inject
    constructor(
        private val repository: CourseRepository,
    ) {
        suspend operator fun invoke(
            placeIds: List<Long>,
            limit: Int = DEFAULT_LIMIT,
        ): List<String> = repository.getRecommendedTags(placeIds, limit)

        private companion object {
            /** 태그 칩이 화면에서 두어 줄을 넘지 않는 선. 서버 허용 범위는 1..30. */
            const val DEFAULT_LIMIT = 10
        }
    }

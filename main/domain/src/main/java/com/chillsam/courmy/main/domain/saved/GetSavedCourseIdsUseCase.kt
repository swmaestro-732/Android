package com.chillsam.courmy.main.domain.saved

import javax.inject.Inject

/**
 * 저장한 코스 id 집합 조회. 다른 화면(홈 피드)의 북마크 아이콘 표시에 쓴다.
 *
 * **정확도 한계**: 서버가 한 번에 최대 [MAX_SIZE] 건만 준다. 저장한 코스가 그보다 많으면
 * 오래된 것이 빠져 일부 카드가 저장 안 된 것처럼 보인다. 커서로 더 돌 수 있으나
 * 홈 진입마다 호출이 늘어 지금은 한 페이지만 본다.
 *
 * TODO-API-SPEC: 홈 피드 응답에 `hasSaved` 가 추가되면 이 UseCase 와 호출부를 제거한다.
 * 코스 상세는 이미 `viewer.hasSaved` 를 받고 있어 같은 방식이면 된다. [wiki-needed]
 */
class GetSavedCourseIdsUseCase
    @Inject
    constructor(
        private val repository: SavedCourseRepository,
    ) {
        suspend operator fun invoke(): Set<String> = repository.getSavedCourseIds(MAX_SIZE)

        companion object {
            /** 서버 상한(1~50)의 최대치 — 한 번에 최대한 많이 받아 대조 정확도를 높인다. */
            const val MAX_SIZE = 50
        }
    }

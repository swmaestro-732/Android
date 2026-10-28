package com.chillsam.courmy.main.domain.saved

import javax.inject.Inject

/**
 * 저장한 코스 id 집합 조회. 다른 화면(홈 피드)의 북마크 아이콘 표시에 쓴다.
 *
 * 목록 화면과 달리 **전부** 필요하다 — 한 페이지만 보면 그 밖의 코스가 저장 안 된 것처럼 보인다.
 * 그래서 커서를 따라 끝까지 돈다. 서버가 커서를 잘못 주면 같은 페이지를 무한히 요청하게 되므로
 * [MAX_PAGES] 로 상한을 둔다(최대 [MAX_SIZE] × [MAX_PAGES] 건).
 *
 * TODO-API-SPEC: 홈 피드 응답에 `hasSaved` 가 추가되면 이 UseCase 와 호출부를 제거한다.
 * 코스 상세는 이미 `viewer.hasSaved` 를 받고 있어 같은 방식이면 된다. [wiki-needed]
 */
class GetSavedCourseIdsUseCase
    @Inject
    constructor(
        private val repository: SavedCourseRepository,
    ) {
        suspend operator fun invoke(): Set<String> {
            val ids = mutableSetOf<String>()
            var cursor: String? = null
            repeat(MAX_PAGES) {
                val page = repository.getSavedCourseIds(MAX_SIZE, cursor)
                ids += page.items
                cursor = page.nextCursor?.takeIf { page.hasNext } ?: return ids
            }
            return ids
        }

        companion object {
            /** 서버 상한(1~50)의 최대치 — 페이지를 적게 돌수록 홈 진입이 빨라진다. */
            const val MAX_SIZE = 50

            /** 커서가 끝나지 않을 때를 대비한 순회 상한. */
            private const val MAX_PAGES = 20
        }
    }

package com.chillsam.courmy.course.domain

import javax.inject.Inject

/**
 * 저장한 장소 방문 처리 UseCase(`PATCH /api/v1/saved-places/{placeId}`).
 *
 * TODO: 저장함 장소 탭에 "방문 처리" UI 가 생기면 거기서 호출한다. 지금은 데이터 계층만 연결돼 있다.
 */
class SetPlaceVisitedUseCase
    @Inject
    constructor(
        private val repository: PlaceSaveRepository,
    ) {
        suspend operator fun invoke(placeId: Long) {
            require(placeId > 0) { "방문 처리할 장소 id 가 없습니다." }
            repository.markVisited(placeId)
        }
    }

package com.chillsam.courmy.course.domain

import javax.inject.Inject

/**
 * 저장한 장소 방문 처리 UseCase(`PATCH /api/v1/saved-places/{placeId}`).
 *
 * 저장한 장소 방문 여부를 갱신하는 데이터 계약이며 현재 화면에는 호출 진입점이 없다.
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

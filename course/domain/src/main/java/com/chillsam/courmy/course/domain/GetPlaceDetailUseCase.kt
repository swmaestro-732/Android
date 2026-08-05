package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.PlaceDetailVO
import javax.inject.Inject

/** 장소 상세 조회 UseCase. 현재는 pass-through 이며, 재시도·도메인 에러 변환이 필요해지면 여기에 추가한다. */
class GetPlaceDetailUseCase
    @Inject
    constructor(
        private val repository: PlaceDetailRepository,
    ) {
        suspend operator fun invoke(
            placeId: Long,
            walkText: String = "",
        ): PlaceDetailVO = repository.getPlaceDetail(placeId, walkText)
    }

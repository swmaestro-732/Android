package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.TraceVisitVO
import javax.inject.Inject

/** 따라가기 중 장소 방문 체크 기록 UseCase. */
class CheckInTracePlaceUseCase
    @Inject
    constructor(
        private val repository: TraceRepository,
    ) {
        suspend operator fun invoke(
            traceId: Long,
            visit: TraceVisitVO,
        ) {
            require(traceId > 0) { "따라가기 기록을 확인해 주세요." }
            repository.checkInPlace(traceId, visit)
        }
    }

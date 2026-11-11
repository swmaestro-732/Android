package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.error.LocalizedValidationException
import com.chillsam.courmy.common.domain.error.requireLocalized
import com.chillsam.courmy.common.domain.helper.StringResource
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
            requireLocalized(traceId > 0) { LocalizedValidationException(StringResource.VALIDATION_TRACE_TARGET) }
            repository.checkInPlace(traceId, visit)
        }
    }

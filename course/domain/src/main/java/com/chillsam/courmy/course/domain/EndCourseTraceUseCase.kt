package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.error.LocalizedValidationException
import com.chillsam.courmy.common.domain.error.requireLocalized
import com.chillsam.courmy.common.domain.helper.StringResource
import com.chillsam.courmy.course.entity.TraceVisitVO
import javax.inject.Inject

/** 따라가기 종료 기록 UseCase. 소요 시간이 음수가 되지 않게 여기서 바로잡는다. */
class EndCourseTraceUseCase
    @Inject
    constructor(
        private val repository: TraceRepository,
    ) {
        suspend operator fun invoke(
            traceId: Long,
            visits: List<TraceVisitVO>,
            durationMinutes: Int,
            distanceMeters: Int = 0,
        ) {
            requireLocalized(traceId > 0) { LocalizedValidationException(StringResource.VALIDATION_TRACE_TARGET) }
            repository.endTrace(
                traceId = traceId,
                visits = visits,
                durationMinutes = durationMinutes.coerceAtLeast(0),
                distanceMeters = distanceMeters.coerceAtLeast(0),
            )
        }
    }

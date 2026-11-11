package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.error.LocalizedValidationException
import com.chillsam.courmy.common.domain.error.requireLocalized
import com.chillsam.courmy.common.domain.helper.StringResource
import com.chillsam.courmy.course.entity.TraceRouteVO
import javax.inject.Inject

/**
 * 따라가기 중 "현재 장소 → 다음 장소" 도보 경로를 구한다.
 *
 * 출발·도착이 같으면(서버가 400 으로 거부) 호출하지 않고 바로 실패시킨다.
 */
class GetTraceRouteUseCase
    @Inject
    constructor(
        private val repository: TraceRouteRepository,
    ) {
        suspend operator fun invoke(
            fromPlaceId: Long,
            toPlaceId: Long,
        ): TraceRouteVO {
            requireLocalized(fromPlaceId > 0 && toPlaceId > 0) {
                LocalizedValidationException(StringResource.VALIDATION_PLACE_TARGET)
            }
            requireLocalized(
                fromPlaceId != toPlaceId,
            ) { LocalizedValidationException(StringResource.VALIDATION_ROUTE_SAME_PLACE) }
            return repository.walkingRoute(fromPlaceId, toPlaceId)
        }
    }

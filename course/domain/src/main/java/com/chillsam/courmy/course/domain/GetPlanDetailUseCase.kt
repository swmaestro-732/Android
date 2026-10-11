package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.PlanDetailVO
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GetPlanDetailUseCase
    @Inject
    constructor(
        private val repository: PlanRepository,
        private val getPlaceDetail: GetPlaceDetailUseCase,
    ) {
        suspend operator fun invoke(planId: Long): PlanDetailVO {
            require(planId > 0) { "계획을 확인해 주세요." }
            val plan = repository.getPlanDetail(planId)
            return coroutineScope {
                plan.copy(
                    places =
                        plan.places
                            .map { place ->
                                async {
                                    val detail =
                                        runCatching { getPlaceDetail(place.placeId) }.getOrElse { error ->
                                            if (error is CancellationException) throw error
                                            null
                                        }
                                    place.copy(detail = detail)
                                }
                            }.awaitAll(),
                )
            }
        }
    }

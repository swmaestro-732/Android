package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.error.LocalizedValidationException
import com.chillsam.courmy.common.domain.error.requireLocalized
import com.chillsam.courmy.common.domain.helper.StringResource
import com.chillsam.courmy.course.entity.PlanInputVO
import java.time.LocalDate
import javax.inject.Inject

/** 공개 코스와 달리 제목은 선택이고 장소 1곳부터 저장할 수 있다. */
class CreatePlanUseCase
    @Inject
    constructor(
        private val repository: PlanRepository,
    ) {
        suspend operator fun invoke(plan: PlanInputVO): Long {
            requireLocalized(
                plan.title.length <= 200,
            ) { LocalizedValidationException(StringResource.VALIDATION_PLAN_TITLE_LENGTH) }
            requireLocalized((plan.memo?.length ?: 0) <= 500) {
                LocalizedValidationException(StringResource.VALIDATION_PLAN_MEMO_LENGTH)
            }
            requireLocalized(
                plan.places.size in 1..10,
            ) { LocalizedValidationException(StringResource.VALIDATION_PLAN_PLACES) }
            requireLocalized(
                plan.sourceCourseId?.let {
                    it > 0
                } != false,
            ) { LocalizedValidationException(StringResource.VALIDATION_ORIGINAL_COURSE) }
            plan.plannedDate?.let { date ->
                val validDate =
                    date.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) &&
                        runCatching { LocalDate.parse(date) }.isSuccess
                requireLocalized(validDate) { LocalizedValidationException(StringResource.VALIDATION_PLAN_DATE) }
            }
            plan.places.forEachIndexed { index, place ->
                requireLocalized(
                    place.placeId > 0,
                ) { LocalizedValidationException(StringResource.VALIDATION_PLACE_INFORMATION) }
                requireLocalized((place.memo?.length ?: 0) <= 500) {
                    LocalizedValidationException(StringResource.VALIDATION_PLACE_MEMO_LENGTH)
                }
                requireLocalized(
                    place.walkingMinutes?.let {
                        it >= -1
                    } != false,
                ) { LocalizedValidationException(StringResource.VALIDATION_WALKING_DURATION) }
                requireLocalized(index == plan.places.lastIndex || place.walkingMinutes != null) {
                    LocalizedValidationException(StringResource.VALIDATION_WALKING_CALCULATE)
                }
            }
            return repository.createPlan(plan)
        }
    }

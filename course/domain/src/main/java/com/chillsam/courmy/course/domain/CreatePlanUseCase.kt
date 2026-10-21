package com.chillsam.courmy.course.domain

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
            require(plan.title.length <= 200) { "계획 제목은 200자까지 입력할 수 있어요." }
            require((plan.memo?.length ?: 0) <= 500) { "계획 메모는 500자까지 입력할 수 있어요." }
            require(plan.places.size in 1..10) { "장소를 1곳 이상, 10곳 이하로 담아주세요." }
            require(plan.sourceCourseId?.let { it > 0 } != false) { "원본 코스를 확인해 주세요." }
            plan.plannedDate?.let { date ->
                val validDate =
                    date.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) &&
                        runCatching { LocalDate.parse(date) }.isSuccess
                require(validDate) {
                    "가려는 날을 yyyy-MM-dd 형식으로 입력해 주세요."
                }
            }
            plan.places.forEachIndexed { index, place ->
                require(place.placeId > 0) { "장소 정보를 확인해 주세요." }
                require((place.memo?.length ?: 0) <= 500) { "장소 메모는 500자까지 입력할 수 있어요." }
                require(place.walkingMinutes?.let { it >= -1 } != false) { "도보 소요시간을 확인해 주세요." }
                require(index == plan.places.lastIndex || place.walkingMinutes != null) { "장소 사이의 도보 소요시간을 계산해 주세요." }
            }
            return repository.createPlan(plan)
        }
    }

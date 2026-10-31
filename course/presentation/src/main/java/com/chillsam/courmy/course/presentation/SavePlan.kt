package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.course.domain.CreatePlanUseCase
import com.chillsam.courmy.course.domain.GetWalkingMinutesUseCase
import com.chillsam.courmy.course.entity.CoursePlaceCoordinate
import com.chillsam.courmy.course.entity.CreateCourseResultVO
import com.chillsam.courmy.course.entity.PlanInputVO
import com.chillsam.courmy.course.entity.PlanPlaceInputVO

/** 저장 시 확정된 방문 순서로 도보 시간을 계산한다. 조회 실패를 0분이나 -1로 대체하지 않는다. */
internal suspend fun savePlan(
    state: CourseCreateUIState,
    createPlan: CreatePlanUseCase,
    getWalkingMinutes: GetWalkingMinutesUseCase,
): CreateCourseResultVO {
    val segments =
        if (state.places.size == 1) {
            emptyList()
        } else {
            require(state.places.all { it.hasLocation }) { "장소의 위치를 확인할 수 없어요." }
            getWalkingMinutes(
                state.places.map {
                    CoursePlaceCoordinate(latitude = it.latitude!!, longitude = it.longitude!!)
                },
            ).also { require(it.size == state.places.size - 1) { "도보 구간 수가 일치하지 않아요." } }
        }
    val id =
        createPlan(
            PlanInputVO(
                title = state.name,
                memo = state.description.takeIf { it.isNotBlank() },
                plannedDate = state.plannedDate.takeIf { it.isNotBlank() },
                sourceCourseId = state.sourceCourseId,
                places =
                    state.places.mapIndexed { index, place ->
                        PlanPlaceInputVO(
                            placeId = place.id.toLong(),
                            memo = place.note.takeIf { it.isNotBlank() },
                            walkingMinutes = segments.getOrNull(index),
                        )
                    },
            ),
        )
    return CreateCourseResultVO(courseId = id, imagesUploaded = true)
}

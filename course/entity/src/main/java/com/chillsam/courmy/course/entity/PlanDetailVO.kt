package com.chillsam.courmy.course.entity

data class PlanDetailVO(
    val planId: Long,
    val title: String,
    val memo: String = "",
    val plannedDate: String = "",
    val sourceCourseId: Long? = null,
    val places: List<PlanDetailPlaceVO> = emptyList(),
)

data class PlanDetailPlaceVO(
    val placeId: Long,
    val memo: String = "",
    val walkingMinutesToNext: Int? = null,
    val detail: PlaceDetailVO? = null,
)

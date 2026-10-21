package com.chillsam.courmy.course.entity

/** 비공개 계획 입력. 장소 목록 순서가 방문 순서이며 코스의 공개 정보는 담지 않는다. */
data class PlanInputVO(
    val title: String = "",
    val memo: String? = null,
    val plannedDate: String? = null,
    val sourceCourseId: Long? = null,
    val places: List<PlanPlaceInputVO>,
)

data class PlanPlaceInputVO(
    val placeId: Long,
    val memo: String? = null,
    val walkingMinutes: Int? = null,
)

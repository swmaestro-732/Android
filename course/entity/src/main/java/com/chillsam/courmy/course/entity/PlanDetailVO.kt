package com.chillsam.courmy.course.entity

data class PlanDetailVO(
    val planId: Long,
    val title: String,
    val memo: String = "",
    val plannedDate: String = "",
    val sourceCourseId: Long? = null,
    val places: List<PlanDetailPlaceVO> = emptyList(),
)

/**
 * 계획 속 장소 한 곳. 이름·카테고리·주소·좌표는 계획 상세 BFF 가 함께 내려준다
 * (별도 장소 상세 조회 없음).
 */
data class PlanDetailPlaceVO(
    val placeId: Long,
    val memo: String = "",
    val walkingMinutesToNext: Int? = null,
    val name: String = "",
    /** 카테고리 한글 라벨(예: "카페·디저트"). */
    val categoryLabel: String = "",
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
)

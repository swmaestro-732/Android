package com.chillsam.courmy.course.entity

/** 복제 코스는 원본의 서로 다른 장소를 최소 두 곳 유지한다. */
object CourseForkRule {
    const val MIN_SHARED_PLACES = 2

    fun requiredKeptPlaceCount(originPlaceCount: Int): Int = if (originPlaceCount == 0) 0 else MIN_SHARED_PLACES
}

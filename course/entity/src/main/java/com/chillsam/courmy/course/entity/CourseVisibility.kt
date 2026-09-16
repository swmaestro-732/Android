package com.chillsam.courmy.course.entity

import kotlinx.serialization.Serializable

/**
 * 코스 공개 범위. Figma FS-34 "③ 공개 설정" 세그먼트.
 * - [PUBLIC]   공개    — 누구나 둘러보고 따라갈 수 있음
 * - [FOLLOWER] 팔로워  — 팔로워에게만 공개
 * - [PRIVATE]  비공개  — 나만 보기
 */
@Serializable
enum class CourseVisibility {
    PUBLIC,
    FOLLOWER,
    PRIVATE,
}

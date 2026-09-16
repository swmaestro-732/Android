package com.chillsam.courmy.course.entity

import kotlinx.serialization.Serializable

/**
 * 저장 완료된 코스 요약. Figma FS-34-Done "코스 완성(저장 성공)" 화면의 표시 데이터.
 *
 * - [category]    카테고리 요약 (예: "성수 · 하루 코스")
 * - [title]       코스 제목 (예: "성수 종일 나들이 코스")
 * - [summaryText] 규모 요약 (예: "6 스팟")
 * - [thumbnailUrl] 대표 이미지. 작성 중 고른 사진이라 아직 로컬 content URI 일 수 있다.
 *                  비어 있으면 플레이스홀더 색만 보인다.
 * - [stops]       코스 동선(방문 순서대로의 장소 목록)
 */
@Serializable
data class CourseCompleteVO(
    val category: String,
    val title: String,
    val summaryText: String,
    val thumbnailUrl: String = "",
    val stops: List<CourseStopVO>,
)

/**
 * 코스 동선의 정류지 1건.
 *
 * - [order]        순번(1부터)
 * - [name]         장소명
 * - [category]     카테고리 (예: "카페 · 베이커리")
 *
 * 장소 썸네일은 두지 않는다. 코스를 막 저장한 시점에는 장소 이미지 URL 이 아직 없어
 * 거의 항상 빈 회색 칸만 늘어섰다.
 */
@Serializable
data class CourseStopVO(
    val order: Int,
    val name: String,
    val category: String,
)

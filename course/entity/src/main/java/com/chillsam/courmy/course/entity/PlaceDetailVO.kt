package com.chillsam.courmy.course.entity

import kotlinx.serialization.Serializable

/**
 * 장소 상세(Figma FS-10-Sheet). 코스 상세의 장소 행에서 화살표를 누르면 하단에서 올라오는
 * 바텀시트가 이 데이터를 표시한다. 표시 전용이며 서버 계약 연동 전까지는 프리뷰 더미로 채운다.
 *
 * - [name]            장소명 (예: "어니언 성수")
 * - [category]        카테고리 (예: "카페 · 베이커리")
 * - [heroImageUrl]    상단 히어로 이미지 URL (없으면 placeholder)
 * - [rating]          평균 별점 텍스트 (예: "4.8")
 * - [reviewCountText] 리뷰 수 (예: "1,240")
 * - [savedCountText]  저장 수 (예: "1.2k")
 * - [isOpen]          영업 중 여부(상태 칩 색·점 강조)
 * - [openStatusText]  영업 상태 문구 (예: "영업중 · 22:00 종료")
 * - [walkText]        도보 안내 (예: "도보 6분")
 * - [areaText]        지역 (예: "성수동")
 * - [imageUrls]       사진 가로 스크롤 목록(없으면 시트에서 placeholder 로 자리만 잡는다)
 * - [tags]            특징 태그 (예: "시그니처 · 팡도르")
 * - [address]         주소 (예: "서울 성동구 아차산로 110")
 * - [hoursText]       영업시간 (예: "매일 11:00 – 22:00")
 */
@Serializable
data class PlaceDetailVO(
    val name: String,
    val category: String,
    val heroImageUrl: String = "",
    val rating: String,
    val reviewCountText: String,
    val savedCountText: String,
    val isOpen: Boolean = true,
    val openStatusText: String,
    val walkText: String,
    val areaText: String,
    val imageUrls: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val address: String,
    val hoursText: String,
)

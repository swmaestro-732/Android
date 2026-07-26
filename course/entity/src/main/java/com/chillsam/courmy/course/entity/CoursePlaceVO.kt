package com.chillsam.courmy.course.entity

import kotlinx.serialization.Serializable

/**
 * 코스에 담긴 장소 1건. Figma FS-34 "② 장소 담기"의 장소 카드.
 *
 * - [id]           장소 고유 식별자
 * - [name]         장소명 (예: "어니언 성수")
 * - [category]     카테고리 (예: "카페 · 베이커리")
 * - [thumbnailUrl] 썸네일 이미지 URL (비어있으면 플레이스홀더)
 * - [note]         이곳에 한마디 (사용자 메모). 비어있으면 입력 유도 placeholder 표시
 * - [photoUrls]    사용자가 담은 사진 URL 목록
 * - [maxPhotos]    사진 최대 개수 (디자인상 6)
 * - [walkText]     다음 장소로의 경로 안내 (예: "도보 9분 · 경로 자동"). 마지막 장소는 빈 문자열
 */
@Serializable
data class CoursePlaceVO(
    val id: String,
    val name: String,
    val category: String = "",
    val thumbnailUrl: String = "",
    val note: String = "",
    val photoUrls: List<String> = emptyList(),
    val maxPhotos: Int = MAX_PHOTOS,
    val walkText: String = "",
) {
    companion object {
        const val MAX_PHOTOS: Int = 6
    }
}

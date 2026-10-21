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
 * - [walkText]     다음 장소로의 경로 안내 (예: "도보 9분"). 마지막 장소는 빈 문자열
 * - [walkingMinutes] 다음 장소까지의 도보 분. [walkText] 의 원본 숫자다. 서버는 도보 시간을 스스로
 *                    계산하지 않고 코스 생성·편집 요청에 실린 값을 그대로 저장하므로, 표시용 문자열만
 *                    갖고 있으면 저장된 코스의 도보 시간이 0 이 된다. 마지막 장소·미계산은 null,
 *                    걸어갈 수 없는 구간은 [UNREACHABLE_ON_FOOT]
 * - [latitude]     위도. 도보 시간 계산(`POST /api/v1/directions/walking`)에 쓴다. 없으면 null
 * - [longitude]    경도. 없으면 null
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
    val walkingMinutes: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
) {
    /** 도보 시간 계산에 넣을 수 있는지. 좌표가 한쪽만 있으면 쓸 수 없다. */
    val hasLocation: Boolean
        get() = latitude != null && longitude != null

    companion object {
        const val MAX_PHOTOS: Int = 6

        /** 걸어서 갈 수 없는 구간을 나타내는 서버 약속값(음수). 분으로 환산하지 않는다. */
        const val UNREACHABLE_ON_FOOT: Int = -1
    }
}

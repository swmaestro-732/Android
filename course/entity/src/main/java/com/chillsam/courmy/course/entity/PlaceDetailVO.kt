package com.chillsam.courmy.course.entity

import kotlinx.serialization.Serializable

/**
 * 장소 상세(Figma FS-10-Sheet). 코스 상세의 장소 행에서 화살표를 누르면 하단에서 올라오는
 * 바텀시트가 이 데이터를 표시한다.
 *
 * `GET /service/v1/places/{placeId}` 가 실제로 내려주는 값만 담는다. 평점·리뷰 수·저장 수·영업 상태·
 * 영업시간·사진·특징 태그는 응답에 없어 늘 빈 값이었고, 시트가 placeholder 만 그리고 있어 화면과 함께
 * 걷어냈다. 서버가 주기 시작하면 여기에 다시 추가한다. [wiki-needed]
 *
 * - [name]      장소명 (예: "어니언 성수")
 * - [category]  카테고리 (예: "카페 · 베이커리"). 서버의 `categories` 를 " · " 로 이어 붙인 값
 * - [address]   주소 (예: "서울 성동구 아차산로 110")
 * - [latitude]  위도. 좌표가 없으면 null (지도·길찾기를 렌더하지 않는다)
 * - [longitude] 경도. 좌표가 없으면 null
 */
@Serializable
data class PlaceDetailVO(
    val name: String,
    val category: String = "",
    val address: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
) {
    /** 지도·길찾기를 그릴 수 있는지. 위경도가 모두 있어야 한다. */
    val hasLocation: Boolean
        get() = latitude != null && longitude != null
}

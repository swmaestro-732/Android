package com.chillsam.courmy.main.entity.saved

/**
 * 저장함 "저장한 장소" 1건. `GET /service/v1/my/saved-places` 응답을 변환한 결과다.
 *
 * [placeId] 는 저장 레코드 id 가 아니라 **장소 id** 다 — 저장 취소·방문 처리 요청의 키다.
 * [categoryLabel] 은 서버 카테고리 코드(예: "CAFE")를 한글 라벨(예: "카페·디저트")로 바꾼 값이다.
 * [visited] 는 "방문함" 표시용이다(PATCH `/api/v1/saved-places/{placeId}` 로 바꾼다).
 */
data class SavedPlaceVO(
    val placeId: Long,
    val name: String,
    val categoryLabel: String = "",
    val area: String = "",
    val imageUrl: String = "",
    val visited: Boolean = false,
)

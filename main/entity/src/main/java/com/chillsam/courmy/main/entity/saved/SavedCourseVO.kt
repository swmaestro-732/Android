package com.chillsam.courmy.main.entity.saved

/**
 * 저장함(FS-14) "저장한 코스" 카드 1건. `GET /service/v1/my/saved-courses` 응답을 변환한 결과다.
 *
 * [id] 는 저장 레코드가 아니라 **코스 id** 다(카드를 누르면 코스 상세로 가고, 저장 취소도 코스 id 로 한다).
 * [tagLabel] 은 카드 상단 좌측 칩(예: "성수 · 데이트")이며 지역·테마가 모두 없으면 빈 문자열이다.
 * [placeLabel] 은 "장소 4곳" 형태다.
 */
data class SavedCourseVO(
    val id: String,
    val tagLabel: String,
    val title: String,
    val placeLabel: String,
    val authorHandle: String,
    val thumbnailUrl: String = "",
)

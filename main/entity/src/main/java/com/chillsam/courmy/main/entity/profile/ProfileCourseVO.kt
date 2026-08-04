package com.chillsam.courmy.main.entity.profile

import kotlinx.serialization.Serializable

/**
 * 프로필 화면(마이·타유저 공통)의 코스 카드 1건.
 * `GET /service/v1/mypage[/{handle}]` 응답의 `courses[]` 를 data 레이어에서 변환한 결과다.
 *
 * TODO-API-SPEC: Figma(FS-15)의 카드는 "1.2k 따라감" 배지와 "4스팟" 부제를 갖지만,
 * `MyPageCourseResponse` 에는 따라감 수·스팟 수가 없다(likesCnt·savesCnt 만 존재).
 * 두 값은 이미 `CourseStatsResponse(placeCount, tracingCount)` 에 있으므로 마이페이지 코스 카드에도
 * 실어달라고 요청한 뒤, 응답에 추가되면 여기에 필드를 넣고 배지·부제를 되살린다. [wiki-needed]
 */
@Serializable
data class ProfileCourseVO(
    val id: String,
    val title: String,
    val thumbnailUrl: String = "",
)

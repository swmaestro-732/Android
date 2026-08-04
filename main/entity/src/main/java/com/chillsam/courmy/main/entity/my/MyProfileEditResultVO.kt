package com.chillsam.courmy.main.entity.my

import kotlinx.serialization.Serializable

/**
 * 프로필 수정(`PATCH /api/v1/users`) 결과 VO.
 *
 * 서버 응답에 코스 목록이 없어 [MyProfileVO] 와는 다른 타입으로 둔다. 수정 직후 화면은
 * 마이페이지를 다시 조회해 갱신하므로, 이 값은 "무엇이 저장됐는지" 확인용이다.
 *
 * TODO-API-SPEC: 서버 `UpdateProfileRequest`·`AccountProfileResponse` 모두 한 줄 소개(bio)가
 * 없어 수정할 수 없다. 서버에 필드가 생기면 요청·응답·편집 화면 입력을 함께 되살린다. [wiki-needed]
 */
@Serializable
data class MyProfileEditResultVO(
    val id: Long,
    val nickname: String,
    val handle: String,
    val profileImageUrl: String = "",
)

package com.chillsam.courmy.main.data.profile.dto

import com.chillsam.courmy.main.entity.my.MyProfileEditResultVO
import kotlinx.serialization.Serializable

/**
 * `PATCH /api/v1/users`(내 프로필 수정) 요청 DTO.
 *
 * 서버 `UpdateProfileRequest` 와 1:1 이다. 모든 필드가 선택이고 **null 은 "변경 안 함"** 을 뜻한다.
 * 값을 보낼 경우 비어 있으면 서버가 거부하므로(`@Size(min=1)`), 호출부에서 공백을 null 로 바꿔 보낸다.
 */
@Serializable
data class UpdateProfileRequest(
    val nickname: String? = null,
    val handle: String? = null,
    val profileImageUrl: String? = null,
)

/**
 * 프로필 수정 응답 봉투. 공통 계약 `{ code, message, data }`.
 *
 * data 는 서버 `AccountProfileResponse` 로, 마이페이지 응답([MyPageProfileDTO])과 달리
 * 팔로우 관계 플래그(`isFollowing`·`isFollower`)와 코스 목록이 없다(본인 프로필이므로).
 */
@Serializable
data class AccountProfileEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: AccountProfileDTO? = null,
)

@Serializable
data class AccountProfileDTO(
    val id: Long? = null,
    val nickname: String? = null,
    val handle: String? = null,
    val profileImageUrl: String? = null,
    val followersCnt: Int? = null,
    val followingsCnt: Int? = null,
    val coursesCnt: Int? = null,
)

/** `GET /api/v1/users/availability` 응답 봉투 `{ code, message, data: { available } }`. */
@Serializable
data class AvailabilityEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: AvailabilityDTO? = null,
)

@Serializable
data class AvailabilityDTO(
    val available: Boolean = false,
)

/**
 * 수정 결과 변환.
 *
 * 응답에 코스 목록이 없어 [com.chillsam.courmy.main.entity.my.MyProfileVO] 로는 만들 수 없다.
 * 화면 갱신은 마이페이지 재조회가 담당하고, 여기서는 수정이 반영된 값만 돌려준다.
 */
fun AccountProfileDTO.toEditResultVO(): MyProfileEditResultVO =
    MyProfileEditResultVO(
        id = id ?: 0L,
        nickname = nickname.orEmpty(),
        handle = handle.orEmpty(),
        profileImageUrl = profileImageUrl.orEmpty(),
    )

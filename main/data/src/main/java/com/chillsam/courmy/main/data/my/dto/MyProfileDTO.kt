package com.chillsam.courmy.main.data.my.dto

import com.chillsam.courmy.main.entity.my.MyCourseVO
import com.chillsam.courmy.main.entity.my.MyProfileVO
import kotlinx.serialization.Serializable

/**
 * `service/v1/my/profile` 응답 DTO.
 *
 * TODO-API-SPEC: 이 엔드포인트는 api-spec 상 "제안(합의 필요)" 상태이고 응답 JSON 스키마가
 * 아직 확정되지 않았다. 아래 필드는 화면 요구(팔로워·저장·내 코스·통계) 기반 추정치다.
 * 서버 계약 확정 시 필드명/구조를 맞추고 이 주석을 제거한다. [wiki-needed]
 *
 * 봉투는 공통 계약 `{ code, message, data }` 를 따른다(api-integration.md).
 */
@Serializable
data class MyProfileEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: MyProfileScreenDTO? = null,
)

@Serializable
data class MyProfileScreenDTO(
    val user: MyUserDTO? = null,
    val stats: MyStatsDTO? = null,
    val myCourses: List<MyCourseDTO>? = null,
)

@Serializable
data class MyUserDTO(
    val nickname: String? = null,
    val handle: String? = null,
    val bio: String? = null,
    val profileImageUrl: String? = null,
)

@Serializable
data class MyStatsDTO(
    val myCourseCount: Int? = null,
    val followerCount: String? = null,
    val followingCount: String? = null,
)

@Serializable
data class MyCourseDTO(
    val id: Long? = null,
    val title: String? = null,
    val tracingCountLabel: String? = null,
    val placeCount: Int? = null,
    val thumbnailUrl: String? = null,
)

/** DTO → VO 변환(화면 표시 포맷팅 포함). data 레이어 전용. */
fun MyProfileScreenDTO.toVO(): MyProfileVO {
    val user = this.user
    val stats = this.stats
    return MyProfileVO(
        nickname = user?.nickname.orEmpty(),
        handle = user?.handle.orEmpty(),
        bio = user?.bio.orEmpty(),
        myCourseCount = stats?.myCourseCount ?: 0,
        followerCount = stats?.followerCount?.ifBlank { "0" } ?: "0",
        followingCount = stats?.followingCount?.ifBlank { "0" } ?: "0",
        myCourses = myCourses.orEmpty().map { it.toVO() },
    )
}

private fun MyCourseDTO.toVO(): MyCourseVO =
    MyCourseVO(
        id = (id ?: 0L).toString(),
        title = title.orEmpty(),
        tracingLabel = "${tracingCountLabel ?: "0"} 따라감",
        spotLabel = "${placeCount ?: 0}스팟",
        thumbnailUrl = thumbnailUrl.orEmpty(),
    )

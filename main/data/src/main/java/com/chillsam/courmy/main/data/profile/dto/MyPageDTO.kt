package com.chillsam.courmy.main.data.profile.dto

import com.chillsam.courmy.main.entity.my.MyProfileVO
import com.chillsam.courmy.main.entity.profile.ProfileCourseVO
import com.chillsam.courmy.main.entity.user.FollowRelation
import com.chillsam.courmy.main.entity.user.UserProfileVO
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * `GET /service/v1/mypage`(나) · `GET /service/v1/mypage/{handle}`(타인) 공통 응답 DTO.
 * 두 엔드포인트가 같은 `MyPageScreenResponse` 형태를 쓰므로 DTO 도 하나로 둔다.
 * 봉투는 공통 계약 `{ code, message, data }`.
 */
@Serializable
data class MyPageEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: MyPageScreenDTO? = null,
)

@Serializable
data class MyPageScreenDTO(
    val profile: MyPageProfileDTO? = null,
    val courses: List<MyPageCourseDTO>? = null,
)

/**
 * 프로필 카드.
 *
 * `isFollowing`·`isFollower` 는 서버가 Kotlin `val isFollowing: Boolean` 로 선언하지만, Jackson 의
 * boolean getter 규칙상 `is` 접두사가 떨어져 `following`·`follower` 로 나갈 수 있다
 * (배포된 swagger 의 `SocialLoginResponse.isNewUser` → `newUser` 가 그 사례).
 * Json 설정이 `ignoreUnknownKeys = true` 라 키가 어긋나면 조용히 false 로 떨어져
 * 팔로우 버튼이 항상 "팔로우하기"로 보이는 무증상 버그가 되므로, [JsonNames] 로 두 표기를 모두 받는다.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class MyPageProfileDTO(
    val id: Long? = null,
    val nickname: String? = null,
    val handle: String? = null,
    val profileImageUrl: String? = null,
    @JsonNames("following")
    val isFollowing: Boolean = false,
    @JsonNames("follower")
    val isFollower: Boolean = false,
    val followersCnt: Int? = null,
    val followingsCnt: Int? = null,
    val coursesCnt: Int? = null,
)

/**
 * 코스 카드. `likesCnt`·`savesCnt`·`theme`·`createdAt` 은 현재 화면에서 쓰지 않는다
 * (Figma 의 "따라감"·"스팟" 수가 응답에 없어 배지·부제를 렌더하지 않기로 함 — [ProfileCourseVO] 주석 참고).
 */
@Serializable
data class MyPageCourseDTO(
    val id: String? = null,
    val title: String? = null,
    val coverImageUrl: String? = null,
    val theme: String? = null,
    val likesCnt: Int? = null,
    val savesCnt: Int? = null,
    val createdAt: String? = null,
)

/** 팔로우/언팔로우 응답 `{ isFollowing, followersCnt }`. 키 표기 이슈는 [MyPageProfileDTO] 와 동일. */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class FollowEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: FollowDataDTO? = null,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class FollowDataDTO(
    @JsonNames("following")
    val isFollowing: Boolean = false,
    val followersCnt: Int? = null,
)

/** 타유저 프로필 변환. [myUserId] 는 JWT 에서 얻은 내 id 로, 자기 자신 여부 판정에만 쓴다. */
fun MyPageScreenDTO.toUserProfileVO(myUserId: Long?): UserProfileVO {
    val profile = this.profile
    val id = profile?.id ?: 0L
    return UserProfileVO(
        id = id,
        nickname = profile?.nickname.orEmpty(),
        handle = profile?.handle.orEmpty(),
        profileImageUrl = profile?.profileImageUrl.orEmpty(),
        courseCount = profile?.coursesCnt ?: 0,
        followerCount = formatCount(profile?.followersCnt ?: 0),
        followingCount = formatCount(profile?.followingsCnt ?: 0),
        relation =
            FollowRelation.of(
                isFollowing = profile?.isFollowing ?: false,
                isFollower = profile?.isFollower ?: false,
            ),
        isMe = myUserId != null && myUserId == id,
        courses = courses.orEmpty().map { it.toVO() },
    )
}

/** 내 프로필 변환. bio 는 서버 응답에 없어 항상 빈 문자열이다([MyProfileVO] 주석 참고). */
fun MyPageScreenDTO.toMyProfileVO(): MyProfileVO {
    val profile = this.profile
    return MyProfileVO(
        id = profile?.id ?: 0L,
        nickname = profile?.nickname.orEmpty(),
        handle = profile?.handle.orEmpty(),
        bio = "",
        profileImageUrl = profile?.profileImageUrl.orEmpty(),
        myCourseCount = profile?.coursesCnt ?: 0,
        followerCount = formatCount(profile?.followersCnt ?: 0),
        followingCount = formatCount(profile?.followingsCnt ?: 0),
        myCourses = courses.orEmpty().map { it.toVO() },
    )
}

private fun MyPageCourseDTO.toVO(): ProfileCourseVO =
    ProfileCourseVO(
        id = id.orEmpty(),
        title = title.orEmpty(),
        thumbnailUrl = coverImageUrl.orEmpty(),
    )

/**
 * 통계 숫자 표시 포맷(Figma FS-15: 1400 → "1.4k", 312 → "312").
 * 소수점 첫째 자리에서 버리며, 정확한 값보다 자릿수 안정성을 우선한다.
 */
internal fun formatCount(value: Int): String =
    when {
        value >= 1_000_000 -> "${trimTrailingZero(value / 1_000_000.0)}m"
        value >= 1_000 -> "${trimTrailingZero(value / 1_000.0)}k"
        else -> value.toString()
    }

private fun trimTrailingZero(value: Double): String {
    val truncated = (value * 10).toInt() / 10.0
    return if (truncated % 1.0 == 0.0) truncated.toInt().toString() else truncated.toString()
}

/** `PATCH /api/v1/users` 요청. null 인 필드는 서버가 건드리지 않는다(부분 수정). */
@Serializable
data class UpdateProfileRequest(
    val nickname: String? = null,
    val handle: String? = null,
    val profileImageUrl: String? = null,
)

@Serializable
data class UpdateProfileEnvelope(
    val code: Int? = null,
    val message: String? = null,
)

package com.chillsam.courmy.main.data.profile.dto

import com.chillsam.courmy.common.entity.paging.CursorPageVO
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
    /** 코스 목록의 다음 페이지 커서. 서버가 만든 불투명 문자열이라 해석하지 않고 되돌려준다. */
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
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
    /** 한 줄 소개. 미설정이면 null 로 온다. */
    val bio: String? = null,
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
        bio = profile?.bio.orEmpty(),
        isMe = myUserId != null && myUserId == id,
        courses = toCoursePage(),
    )
}

/**
 * 코스 목록 한 페이지. 서버가 `hasNext=true` 인데 커서를 안 주는 경우에도 무한 재요청에 빠지지 않도록,
 * 커서가 없으면 끝으로 처리한다(다른 목록 API 의 `toPageVO` 와 같은 방어).
 */
private fun MyPageScreenDTO.toCoursePage(): CursorPageVO<ProfileCourseVO> =
    CursorPageVO(
        items = courses.orEmpty().map { it.toVO() },
        nextCursor = nextCursor,
        hasNext = hasNext && !nextCursor.isNullOrBlank(),
    )

/** 내 프로필 변환. */
fun MyPageScreenDTO.toMyProfileVO(): MyProfileVO {
    val profile = this.profile
    return MyProfileVO(
        id = profile?.id ?: 0L,
        nickname = profile?.nickname.orEmpty(),
        handle = profile?.handle.orEmpty(),
        bio = profile?.bio.orEmpty(),
        profileImageUrl = profile?.profileImageUrl.orEmpty(),
        myCourseCount = profile?.coursesCnt ?: 0,
        followerCount = formatCount(profile?.followersCnt ?: 0),
        followingCount = formatCount(profile?.followingsCnt ?: 0),
        myCourses = toCoursePage(),
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

/**
 * `PATCH /api/v1/users` 요청. null 인 필드는 서버가 건드리지 않는다(부분 수정).
 *
 * [areaCodes]·[likeThemes] 는 **전체 치환**이다 — 빈 배열이면 전부 해제, null 이면 유지.
 * [likeThemes] 는 코스 카테고리 이름(`CAFETOUR` …)이고 서버가 enum 으로 검증해 모르는 값은
 * `400 "존재하지 않는 관심 테마가 포함되어 있습니다"` 로 거부한다(라벨을 보내면 안 된다).
 *
 * 주의: 이 엔드포인트는 **모르는 필드를 400 없이 조용히 무시**한다. 필드명을 틀리면 200 을 받고도
 * 값만 반영되지 않아 알아채기 어려우므로, 이름을 서버 DTO 와 정확히 맞춘다.
 */
@Serializable
data class UpdateProfileRequest(
    val nickname: String? = null,
    val handle: String? = null,
    val profileImageUrl: String? = null,
    val bio: String? = null,
    val areaCodes: List<String>? = null,
    val likeThemes: List<String>? = null,
)

/**
 * `PATCH /api/v1/users` 응답 봉투.
 *
 * TODO-API-SPEC: `data`(`AccountProfileResponse`)에 서버가 정규화한 최종 값
 * (`bio`·`likeThemes`·`areas`)이 담겨 오는데 지금은 받지 않는다. 관심 지역·테마를 **되읽을 수 있는
 * 유일한 경로**이기도 하다(마이페이지 응답에는 없다). 다만 `areas` 는 `{code, name}` 뿐이라
 * 화면이 쓰는 `AreaVO`(shortName·fullName)를 복원할 수 없어, 지역 조회 API 가 생길 때 함께
 * 정리하는 편이 낫다. [wiki-needed]
 */
@Serializable
data class UpdateProfileEnvelope(
    val code: Int? = null,
    val message: String? = null,
)

package com.chillsam.courmy.main.entity.user

import com.chillsam.courmy.main.entity.profile.ProfileCourseVO
import kotlinx.serialization.Serializable

/**
 * 타유저 프로필(FS-15 OtherUserPageActivity) 표시 데이터(VO).
 * `GET /service/v1/mypage/{handle}` 응답을 data 레이어의 `toVO()` 에서 변환한 결과다.
 *
 * [isMe] 는 응답의 `id` 와 현재 로그인 사용자 id(JWT sub)를 data 레이어에서 비교해 채운다.
 * true 면 자기 자신을 연 것이므로 팔로우 버튼을 숨긴다.
 *
 * TODO-API-SPEC: Figma 는 닉네임 아래 한 줄 소개(bio)를 보여주지만 서버 응답에 해당 필드가 없어
 * 현재는 렌더하지 않는다. `MyPageProfileResponse` 에 bio 가 추가되면 필드와 UI 를 함께 되살린다. [wiki-needed]
 */
@Serializable
data class UserProfileVO(
    val id: Long,
    val nickname: String,
    val handle: String,
    val profileImageUrl: String = "",
    val courseCount: Int = 0,
    val followerCount: String = "0",
    val followingCount: String = "0",
    val relation: FollowRelation = FollowRelation.NONE,
    val isMe: Boolean = false,
    val courses: List<ProfileCourseVO> = emptyList(),
) {
    companion object {
        /** 개발/프리뷰용 더미. */
        val sample: UserProfileVO =
            UserProfileVO(
                id = 1L,
                nickname = "User1",
                handle = "happy",
                courseCount = 8,
                followerCount = "1.4k",
                followingCount = "312",
                relation = FollowRelation.NONE,
                courses =
                    listOf(
                        ProfileCourseVO("1", "비 오는 날 성수 카페 코스"),
                        ProfileCourseVO("2", "연남 브런치 산책"),
                        ProfileCourseVO("3", "한남 갤러리 도장깨기"),
                        ProfileCourseVO("4", "서울숲 아침 산책"),
                    ),
            )
    }
}

package com.chillsam.courmy.main.entity.user

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.profile.ProfileCourseVO
import kotlinx.serialization.Serializable

/**
 * 타유저 프로필(FS-15 OtherUserPageActivity) 표시 데이터(VO).
 * `GET /service/v1/mypage/{handle}` 응답을 data 레이어의 `toVO()` 에서 변환한 결과다.
 *
 * [isMe] 는 응답의 `id` 와 현재 로그인 사용자 id(JWT sub)를 data 레이어에서 비교해 채운다.
 * true 면 자기 자신을 연 것이므로 팔로우 버튼을 숨긴다.
 */
@Serializable
data class UserProfileVO(
    val id: Long,
    val nickname: String,
    val handle: String,
    /** 한 줄 소개. 미설정이면 빈 문자열이라 호출부가 줄을 그리지 않는다. */
    val bio: String = "",
    val profileImageUrl: String = "",
    val courseCount: Int = 0,
    val followerCount: String = "0",
    val followingCount: String = "0",
    val relation: FollowRelation = FollowRelation.NONE,
    val isMe: Boolean = false,
    /** 공개 코스 **한 페이지**. 누적은 ViewModel 이 맡는다(홈 피드와 같은 방식). */
    val courses: CursorPageVO<ProfileCourseVO> = CursorPageVO(),
)

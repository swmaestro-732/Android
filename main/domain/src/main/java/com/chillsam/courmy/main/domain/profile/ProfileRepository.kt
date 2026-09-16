package com.chillsam.courmy.main.domain.profile

import com.chillsam.courmy.main.entity.area.AreaVO
import com.chillsam.courmy.main.entity.my.MyProfileVO
import com.chillsam.courmy.main.entity.user.FollowResultVO
import com.chillsam.courmy.main.entity.user.UserProfileVO

/**
 * 마이페이지(나·타인) 조회와 팔로우 계약. 구현은 data 레이어이며 실패 시 예외를 throw 한다.
 * 두 조회가 같은 BFF 응답(`/service/v1/mypage[/{handle}]`)을 쓰므로 한 인터페이스에 둔다.
 */
interface ProfileRepository {
    /**
     * 내 마이페이지. 사용자는 JWT 로 식별된다.
     *
     * 코스 목록은 커서 페이징이다. [cursor] 가 null 이면 첫 페이지다. 서버에 "코스만" 주는 API 가 없어
     * 다음 페이지를 받을 때도 프로필이 함께 오는데, 호출부는 코스 페이지만 이어 붙이면 된다.
     */
    suspend fun getMyProfile(
        size: Int,
        cursor: String? = null,
    ): MyProfileVO

    /** 타유저 마이페이지. 팔로우 관계는 JWT 의 뷰어 기준으로 채워진다. 페이징은 [getMyProfile] 과 같다. */
    suspend fun getUserProfile(
        handle: String,
        size: Int,
        cursor: String? = null,
    ): UserProfileVO

    /** 대상 사용자 팔로우/언팔로우. [follow] 가 true 면 팔로우, false 면 언팔로우. */
    suspend fun setFollow(
        userId: Long,
        follow: Boolean,
    ): FollowResultVO

    /**
     * 관심 테마 저장. 서버에 보내고(전체 치환) 기기 캐시도 함께 갱신한다.
     *
     * [themes] 는 코스 카테고리 **코드**여야 한다(`CourseCategoryVO.name`) — 서버가 enum 으로 검증한다.
     *
     * TODO-API-SPEC: 저장은 서버가 받지만 **되읽을 조회 경로가 없다** — 마이페이지 응답에
     * `likeThemes` 가 없어 표시는 기기 캐시로 한다. 응답에 실리면 캐시를 지운다. [wiki-needed]
     */
    suspend fun saveInterestThemes(themes: List<String>)

    /**
     * 관심 지역 저장. 서버에는 [AreaVO.code] 만 보내고(전체 치환), 이름까지 필요한 표시용으로
     * 기기 캐시도 갱신한다. [saveInterestThemes] 와 같은 조회 경로 부재 사정이다. [wiki-needed]
     */
    suspend fun saveInterestRegions(regions: List<AreaVO>)

    /**
     * 관심사를 기기 캐시에만 기록한다(서버 요청 없음).
     *
     * 회원가입 전용이다 — 가입 요청(`SignupRequest`)이 이미 관심사를 서버에 실어 보내므로 다시
     * PATCH 하면 중복이다. 그런데도 캐시가 필요한 이유는 조회 경로가 없어서다. [wiki-needed]
     */
    suspend fun cacheInterests(
        themes: List<String>,
        regions: List<AreaVO>,
    )

    /**
     * 내 프로필 부분 수정. null 인 항목은 건드리지 않는다.
     * [bio] 는 빈 문자열이면 "지움"으로 서버에 반영된다.
     */
    suspend fun updateProfile(
        nickname: String? = null,
        handle: String? = null,
        profileImageUrl: String? = null,
        bio: String? = null,
    )
}

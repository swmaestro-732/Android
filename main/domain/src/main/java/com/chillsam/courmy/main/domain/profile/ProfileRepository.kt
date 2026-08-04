package com.chillsam.courmy.main.domain.profile

import com.chillsam.courmy.main.entity.my.MyProfileEditResultVO
import com.chillsam.courmy.main.entity.my.MyProfileVO
import com.chillsam.courmy.main.entity.user.FollowResultVO
import com.chillsam.courmy.main.entity.user.UserProfileVO

/**
 * 마이페이지(나·타인) 조회와 팔로우 계약. 구현은 data 레이어이며 실패 시 예외를 throw 한다.
 * 두 조회가 같은 BFF 응답(`/service/v1/mypage[/{handle}]`)을 쓰므로 한 인터페이스에 둔다.
 */
interface ProfileRepository {
    /** 내 마이페이지. 사용자는 JWT 로 식별된다. */
    suspend fun getMyProfile(): MyProfileVO

    /** 타유저 마이페이지. 팔로우 관계는 JWT 의 뷰어 기준으로 채워진다. */
    suspend fun getUserProfile(handle: String): UserProfileVO

    /** 대상 사용자 팔로우/언팔로우. [follow] 가 true 면 팔로우, false 면 언팔로우. */
    suspend fun setFollow(
        userId: Long,
        follow: Boolean,
    ): FollowResultVO

    /**
     * 내 프로필 부분 수정. **null 인 인자는 변경하지 않는다**(서버가 넘어온 필드만 반영).
     * 사용자는 JWT 로 식별한다.
     */
    suspend fun updateProfile(
        nickname: String? = null,
        handle: String? = null,
        profileImageUrl: String? = null,
    ): MyProfileEditResultVO

    /**
     * 로컬 이미지를 업로드하고 공개 URL 을 돌려준다.
     * [updateProfile] 의 `profileImageUrl` 은 URL 문자열만 받으므로, 기기에서 고른 사진은
     * 먼저 이 함수로 올려 URL 을 얻어야 한다.
     */
    suspend fun uploadProfileImage(localUri: String): String

    /** 핸들(아이디) 사용 가능 여부. 예약어이거나 이미 사용 중이면 false. */
    suspend fun isHandleAvailable(handle: String): Boolean
}

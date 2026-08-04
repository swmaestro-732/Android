package com.chillsam.courmy.main.data.profile

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.main.data.profile.dto.FollowEnvelope
import com.chillsam.courmy.main.data.profile.dto.MyPageEnvelope
import com.chillsam.courmy.main.data.profile.dto.UpdateProfileEnvelope
import com.chillsam.courmy.main.data.profile.dto.UpdateProfileRequest

class ProfileDataSource(
    private val apiService: ProfileApiService,
) : BaseRemoteDataSource() {
    suspend fun getMyPage(): MyPageEnvelope = checkResponse(apiService.getMyPage())

    suspend fun getUserPage(handle: String): MyPageEnvelope = checkResponse(apiService.getUserPage(handle))

    suspend fun follow(userId: Long): FollowEnvelope = checkResponse(apiService.follow(userId))

    suspend fun unfollow(userId: Long): FollowEnvelope = checkResponse(apiService.unfollow(userId))

    /**
     * 프로필 수정. 기본 경로가 "경로 없음"으로 실패하면 develop 의 새 경로로 한 번 더 시도한다
     * ([ProfileApiService.updateProfileFallback] 참고).
     */
    suspend fun updateProfile(
        userId: Long,
        request: UpdateProfileRequest,
    ): UpdateProfileEnvelope {
        val primary = apiService.updateProfile(userId, request)
        if (!primary.isSuccessful && primary.code() in PATH_MISSING_CODES) {
            return checkResponse(apiService.updateProfileFallback(request))
        }
        return checkResponse(primary)
    }

    private companion object {
        /** 경로가 없다고 판단할 상태 코드. 미매핑 경로에 500 을 주는 서버 동작을 포함. */
        val PATH_MISSING_CODES = setOf(404, 405, 500)
    }
}

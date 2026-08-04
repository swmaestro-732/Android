package com.chillsam.courmy.main.domain.user

import com.chillsam.courmy.main.domain.profile.ProfileRepository
import com.chillsam.courmy.main.entity.user.UserProfileVO
import javax.inject.Inject

/** 타유저 프로필 조회 UseCase. 현재는 pass-through 이며, 재시도·도메인 에러 변환이 필요해지면 여기에 추가한다. */
class GetUserProfileUseCase
    @Inject
    constructor(
        private val repository: ProfileRepository,
    ) {
        suspend operator fun invoke(handle: String): UserProfileVO = repository.getUserProfile(handle)
    }

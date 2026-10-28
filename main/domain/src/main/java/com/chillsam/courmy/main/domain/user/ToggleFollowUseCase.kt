package com.chillsam.courmy.main.domain.user

import com.chillsam.courmy.main.domain.profile.ProfileRepository
import com.chillsam.courmy.main.entity.user.FollowResultVO
import javax.inject.Inject

/**
 * 팔로우 토글 UseCase. 현재 관계([currentlyFollowing])의 반대로 요청하며,
 * 서버가 내려준 갱신 상태·팔로워 수를 그대로 돌려준다.
 */
class ToggleFollowUseCase
    @Inject
    constructor(
        private val repository: ProfileRepository,
    ) {
        suspend operator fun invoke(
            userId: Long,
            currentlyFollowing: Boolean,
        ): FollowResultVO = repository.setFollow(userId = userId, follow = !currentlyFollowing)
    }

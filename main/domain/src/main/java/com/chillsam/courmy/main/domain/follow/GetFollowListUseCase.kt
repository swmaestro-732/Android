package com.chillsam.courmy.main.domain.follow

import com.chillsam.courmy.main.entity.my.FollowUserVO
import javax.inject.Inject

/** 내 팔로워/팔로잉 목록 조회. 서버가 size 를 1~50 으로 제한하므로 범위를 맞춰 넘긴다. */
class GetFollowListUseCase
    @Inject
    constructor(
        private val repository: FollowListRepository,
    ) {
        suspend operator fun invoke(
            followers: Boolean,
            size: Int = DEFAULT_SIZE,
        ): List<FollowUserVO> {
            val bounded = size.coerceIn(MIN_SIZE, MAX_SIZE)
            return if (followers) repository.getMyFollowers(bounded) else repository.getMyFollowings(bounded)
        }

        companion object {
            const val DEFAULT_SIZE = 50
            private const val MIN_SIZE = 1
            private const val MAX_SIZE = 50
        }
    }

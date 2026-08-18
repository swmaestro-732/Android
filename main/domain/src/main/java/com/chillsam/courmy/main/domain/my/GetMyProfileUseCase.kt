package com.chillsam.courmy.main.domain.my

import com.chillsam.courmy.main.domain.profile.ProfileRepository
import com.chillsam.courmy.main.entity.my.MyProfileVO
import javax.inject.Inject

/**
 * 마이·프로필 조회 UseCase.
 *
 * 코스 목록은 커서 페이징이다. 서버가 `size` 를 1~50 으로 제한하므로 범위를 맞춰 넘긴다
 * (저장함·홈 피드와 같은 규칙). [cursor] 가 null 이면 첫 페이지다.
 */
class GetMyProfileUseCase
    @Inject
    constructor(
        private val repository: ProfileRepository,
    ) {
        suspend operator fun invoke(
            size: Int = DEFAULT_SIZE,
            cursor: String? = null,
        ): MyProfileVO = repository.getMyProfile(size = size.coerceIn(MIN_SIZE, MAX_SIZE), cursor = cursor)

        companion object {
            const val DEFAULT_SIZE = 20
            private const val MIN_SIZE = 1
            private const val MAX_SIZE = 50
        }
    }

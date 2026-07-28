package com.chillsam.courmy.main.domain.my

import com.chillsam.courmy.main.entity.my.MyProfileVO
import javax.inject.Inject

/**
 * 마이·프로필 조회 UseCase. 현재는 pass-through 이며,
 * 재시도·도메인 에러 변환이 필요해지면 여기에 추가한다.
 */
class GetMyProfileUseCase
    @Inject
    constructor(
        private val repository: MyProfileRepository,
    ) {
        suspend operator fun invoke(): MyProfileVO = repository.getMyProfile()
    }

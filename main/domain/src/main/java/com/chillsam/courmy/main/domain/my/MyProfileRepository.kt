package com.chillsam.courmy.main.domain.my

import com.chillsam.courmy.main.entity.my.MyProfileVO

/** 마이·프로필 조회 계약. 구현은 data 레이어([MyProfileRepositoryImpl]), 실패 시 예외 throw. */
interface MyProfileRepository {
    /** `service/v1/my/profile`(BFF) 조회. */
    suspend fun getMyProfile(): MyProfileVO
}

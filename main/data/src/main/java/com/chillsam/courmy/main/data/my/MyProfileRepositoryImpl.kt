package com.chillsam.courmy.main.data.my

import com.chillsam.courmy.main.data.my.dto.toVO
import com.chillsam.courmy.main.domain.my.MyProfileRepository
import com.chillsam.courmy.main.entity.my.MyProfileVO

class MyProfileRepositoryImpl(
    private val dataSource: MyProfileDataSource,
) : MyProfileRepository {
    override suspend fun getMyProfile(): MyProfileVO {
        val envelope = dataSource.getMyProfile()
        val data = requireNotNull(envelope.data) { "마이 프로필 응답에 data 가 없습니다." }
        return data.toVO()
    }
}

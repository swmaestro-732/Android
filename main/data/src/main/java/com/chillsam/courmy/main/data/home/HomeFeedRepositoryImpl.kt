package com.chillsam.courmy.main.data.home

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.data.home.dto.toPageVO
import com.chillsam.courmy.main.data.home.dto.toVO
import com.chillsam.courmy.main.domain.home.HomeFeedRepository
import com.chillsam.courmy.main.entity.home.HomeCourseVO
import com.chillsam.courmy.main.entity.home.HomeVO

class HomeFeedRepositoryImpl(
    private val dataSource: HomeFeedDataSource,
) : HomeFeedRepository {
    override suspend fun getCourseFeed(
        size: Int,
        cursor: String?,
    ): CursorPageVO<HomeCourseVO> {
        val envelope = dataSource.getCourseFeed(size, cursor)
        val data =
            requireNotNull(envelope.data) {
                envelope.message ?: "코스 피드 응답에 data 가 없습니다."
            }
        return data.toPageVO()
    }

    override suspend fun getHome(
        userLat: Double?,
        userLng: Double?,
    ): HomeVO {
        val envelope = dataSource.getHome(userLat, userLng)
        val data =
            requireNotNull(envelope.data) {
                envelope.message ?: "홈 응답에 data 가 없습니다."
            }
        return data.toVO()
    }
}

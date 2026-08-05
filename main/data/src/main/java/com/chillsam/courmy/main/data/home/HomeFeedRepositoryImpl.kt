package com.chillsam.courmy.main.data.home

import com.chillsam.courmy.main.data.home.dto.toVOList
import com.chillsam.courmy.main.domain.home.HomeFeedRepository
import com.chillsam.courmy.main.entity.home.HomeCourseVO

class HomeFeedRepositoryImpl(
    private val dataSource: HomeFeedDataSource,
) : HomeFeedRepository {
    override suspend fun getCourseFeed(size: Int): List<HomeCourseVO> {
        val envelope = dataSource.getCourseFeed(size)
        val data =
            requireNotNull(envelope.data) {
                envelope.message ?: "코스 피드 응답에 data 가 없습니다."
            }
        return data.toVOList()
    }
}

package com.chillsam.courmy.course.data.placeDetail

import com.chillsam.courmy.course.data.placeDetail.dto.toVO
import com.chillsam.courmy.course.domain.PlaceDetailRepository
import com.chillsam.courmy.course.entity.PlaceDetailVO

class PlaceDetailRepositoryImpl(
    private val dataSource: PlaceDetailDataSource,
) : PlaceDetailRepository {
    override suspend fun getPlaceDetail(
        placeId: Long,
        walkText: String,
    ): PlaceDetailVO {
        val envelope = dataSource.getPlaceDetail(placeId)
        val data =
            requireNotNull(envelope.data) {
                envelope.message ?: "장소 상세 응답에 data 가 없습니다."
            }
        return data.toVO(walkText)
    }
}

package com.chillsam.courmy.course.data.place

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.course.data.place.dto.toPageVO
import com.chillsam.courmy.course.data.place.dto.toVOList
import com.chillsam.courmy.course.domain.PlaceRepository
import com.chillsam.courmy.course.entity.CoursePlaceVO

class PlaceRepositoryImpl(
    private val dataSource: PlaceDataSource,
) : PlaceRepository {
    override suspend fun searchPlaces(
        query: String,
        cursor: String?,
    ): CursorPageVO<CoursePlaceVO> =
        dataSource
            .searchPlaces(query, cursor)
            .data
            ?.toPageVO()
            ?: CursorPageVO()

    override suspend fun searchExternalPlaces(query: String): List<CoursePlaceVO> =
        dataSource
            .searchExternalPlaces(query)
            .data
            ?.toVOList()
            .orEmpty()
}

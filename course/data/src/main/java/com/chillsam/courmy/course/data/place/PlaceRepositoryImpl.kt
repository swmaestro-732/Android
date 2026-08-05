package com.chillsam.courmy.course.data.place

import com.chillsam.courmy.course.data.place.dto.toVO
import com.chillsam.courmy.course.data.place.dto.toVOList
import com.chillsam.courmy.course.domain.PlaceRepository
import com.chillsam.courmy.course.entity.CoursePlaceVO

class PlaceRepositoryImpl(
    private val dataSource: PlaceDataSource,
) : PlaceRepository {
    override suspend fun searchPlaces(query: String): List<CoursePlaceVO> =
        dataSource
            .searchPlaces(query)
            .data
            ?.places
            .orEmpty()
            .map { it.toVO() }

    override suspend fun searchExternalPlaces(query: String): List<CoursePlaceVO> =
        dataSource
            .searchExternalPlaces(query)
            .data
            ?.toVOList()
            .orEmpty()
}

package com.chillsam.courmy.course.data.placeSave

import com.chillsam.courmy.course.domain.PlaceSaveRepository

class PlaceSaveRepositoryImpl(
    private val dataSource: PlaceSaveDataSource,
) : PlaceSaveRepository {
    override suspend fun setSaved(
        placeId: Long,
        saved: Boolean,
    ) {
        if (saved) dataSource.save(placeId) else dataSource.unsave(placeId)
    }

    override suspend fun markVisited(placeId: Long) {
        dataSource.visit(placeId)
    }
}

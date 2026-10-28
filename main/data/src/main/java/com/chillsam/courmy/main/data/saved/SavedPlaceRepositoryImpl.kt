package com.chillsam.courmy.main.data.saved

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.data.saved.dto.toPageVO
import com.chillsam.courmy.main.domain.saved.SavedPlaceRepository
import com.chillsam.courmy.main.entity.saved.SavedPlaceVO

class SavedPlaceRepositoryImpl(
    private val dataSource: SavedPlaceDataSource,
) : SavedPlaceRepository {
    override suspend fun getSavedPlaces(
        size: Int,
        cursor: String?,
        visited: Boolean?,
    ): CursorPageVO<SavedPlaceVO> {
        val envelope = dataSource.getSavedPlaces(size, cursor, visited)
        val data =
            requireNotNull(envelope.data) {
                envelope.message ?: "저장 장소 응답에 data 가 없습니다."
            }
        return data.toPageVO()
    }
}

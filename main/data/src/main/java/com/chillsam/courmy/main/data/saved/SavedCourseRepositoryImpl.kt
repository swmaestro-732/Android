package com.chillsam.courmy.main.data.saved

import com.chillsam.courmy.main.data.saved.dto.toCourseIdSet
import com.chillsam.courmy.main.data.saved.dto.toVOList
import com.chillsam.courmy.main.domain.saved.SavedCourseRepository
import com.chillsam.courmy.main.entity.saved.SavedCourseVO

class SavedCourseRepositoryImpl(
    private val dataSource: SavedCourseDataSource,
) : SavedCourseRepository {
    override suspend fun getSavedCourses(size: Int): List<SavedCourseVO> {
        val envelope = dataSource.getSavedCourses(size)
        val data =
            requireNotNull(envelope.data) {
                envelope.message ?: "저장 코스 응답에 data 가 없습니다."
            }
        return data.toVOList()
    }

    override suspend fun getSavedCourseIds(size: Int): Set<String> {
        val envelope = dataSource.getSavedCourseIds(size)
        val data =
            requireNotNull(envelope.data) {
                envelope.message ?: "저장 코스 id 응답에 data 가 없습니다."
            }
        return data.toCourseIdSet()
    }
}

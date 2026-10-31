package com.chillsam.courmy.main.data.saved

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.data.saved.dto.toCourseIdSet
import com.chillsam.courmy.main.data.saved.dto.toScreenVO
import com.chillsam.courmy.main.domain.saved.SavedCourseRepository
import com.chillsam.courmy.main.entity.saved.SavedCoursesVO

class SavedCourseRepositoryImpl(
    private val dataSource: SavedCourseDataSource,
) : SavedCourseRepository {
    override suspend fun getSavedCourses(
        size: Int,
        cursor: String?,
        folderId: Long?,
        completed: Boolean?,
    ): SavedCoursesVO {
        val envelope = dataSource.getSavedCourses(size, cursor, folderId, completed)
        val data =
            requireNotNull(envelope.data) {
                envelope.message ?: "저장 코스 응답에 data 가 없습니다."
            }
        return data.toScreenVO()
    }

    override suspend fun getSavedCourseIds(
        size: Int,
        cursor: String?,
    ): CursorPageVO<String> {
        val envelope = dataSource.getSavedCourseIds(size, cursor)
        val data =
            requireNotNull(envelope.data) {
                envelope.message ?: "저장 코스 id 응답에 data 가 없습니다."
            }
        return CursorPageVO(
            items = data.toCourseIdSet().toList(),
            nextCursor = data.nextCursor?.takeIf { it.isNotBlank() },
            hasNext = data.hasNext && !data.nextCursor.isNullOrBlank(),
        )
    }
}

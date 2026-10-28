package com.chillsam.courmy.main.data.search

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.data.search.dto.toPageVO
import com.chillsam.courmy.main.domain.search.CourseSearchRepository
import com.chillsam.courmy.main.entity.search.SearchCourseVO

class CourseSearchRepositoryImpl(
    private val dataSource: CourseSearchDataSource,
) : CourseSearchRepository {
    override suspend fun searchCourses(
        keyword: String,
        size: Int,
        cursor: String?,
    ): CursorPageVO<SearchCourseVO> {
        val envelope = dataSource.searchCourses(keyword = keyword, size = size, cursor = cursor)
        val data =
            requireNotNull(envelope.data) {
                envelope.message ?: "코스 검색 응답에 data 가 없습니다."
            }
        return data.toPageVO()
    }
}

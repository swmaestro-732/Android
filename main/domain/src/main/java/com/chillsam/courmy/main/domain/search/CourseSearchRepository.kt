package com.chillsam.courmy.main.domain.search

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.search.SearchCourseVO

interface CourseSearchRepository {
    suspend fun searchCourses(
        keyword: String,
        size: Int,
        cursor: String? = null,
    ): CursorPageVO<SearchCourseVO>
}

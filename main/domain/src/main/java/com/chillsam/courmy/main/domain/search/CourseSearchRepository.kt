package com.chillsam.courmy.main.domain.search

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.search.SearchCourseVO

interface CourseSearchRepository {
    suspend fun searchCourses(
        keyword: String,
        size: Int,
        cursor: String? = null,
        /** 지역 필터. 코스에 적힌 표시 지역명 그대로 보낸다(예: "서울"). null 이면 전체. */
        area: String? = null,
        /** 코스 태그 필터. 서버 enum 이름(대문자, 예: "FOOD"). null 이면 전체. */
        category: String? = null,
        /** 정렬. 서버 enum 이름(RELEVANCE·LATEST·POPULAR). null 이면 서버 기본(RELEVANCE). */
        sort: String? = null,
    ): CursorPageVO<SearchCourseVO>
}

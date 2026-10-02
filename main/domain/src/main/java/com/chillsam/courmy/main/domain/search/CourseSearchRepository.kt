package com.chillsam.courmy.main.domain.search

import com.chillsam.courmy.main.entity.search.SearchCourseVO

/**
 * 코스 통합 검색(`GET /api/v1/courses/search`) 계약. 구현은 data 레이어이며 실패 시 예외를 throw 한다.
 *
 * 응답이 커서 페이징이지만 검색 결과 화면(FS-18)은 아직 첫 페이지만 보여주므로 목록만 받는다.
 * 더보기 스크롤이 생기면 `CursorPageVO` 반환으로 바꾼다.
 */
interface CourseSearchRepository {
    suspend fun searchCourses(
        keyword: String,
        size: Int,
    ): List<SearchCourseVO>
}

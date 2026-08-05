package com.chillsam.courmy.main.domain.saved

import com.chillsam.courmy.main.entity.saved.SavedCourseVO

/**
 * 저장함 · 코스 탭 계약. 구현은 data 레이어이며 실패 시 예외를 throw 한다.
 * 사용자는 서버가 JWT 로 식별하므로 인자에 id 를 싣지 않는다(로그인 필수).
 */
interface SavedCourseRepository {
    suspend fun getSavedCourses(size: Int): List<SavedCourseVO>

    /** 저장 취소. 저장 레코드가 아니라 **코스 id** 로 지운다. */
    suspend fun unsaveCourse(courseId: Long)
}

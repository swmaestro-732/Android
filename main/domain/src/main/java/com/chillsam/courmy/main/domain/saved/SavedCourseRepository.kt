package com.chillsam.courmy.main.domain.saved

import com.chillsam.courmy.main.entity.saved.SavedCourseVO

/**
 * 저장함 · 코스 탭 **조회** 계약. 구현은 data 레이어이며 실패 시 예외를 throw 한다.
 * 사용자는 서버가 JWT 로 식별하므로 인자에 id 를 싣지 않는다(로그인 필수).
 *
 * 저장/취소는 코스 상세·홈 피드와 같은 동작이라
 * [com.chillsam.courmy.course.domain.CourseSaveRepository] 가 담당한다(중복 구현 방지).
 */
interface SavedCourseRepository {
    suspend fun getSavedCourses(size: Int): List<SavedCourseVO>
}

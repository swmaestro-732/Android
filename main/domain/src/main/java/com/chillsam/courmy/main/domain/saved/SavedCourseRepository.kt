package com.chillsam.courmy.main.domain.saved

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.saved.SavedCourseVO

/**
 * 저장함 · 코스 탭 **조회** 계약. 구현은 data 레이어이며 실패 시 예외를 throw 한다.
 * 사용자는 서버가 JWT 로 식별하므로 인자에 id 를 싣지 않는다(로그인 필수).
 *
 * 저장/취소는 코스 상세·홈 피드와 같은 동작이라
 * [com.chillsam.courmy.course.domain.CourseSaveRepository] 가 담당한다(중복 구현 방지).
 */
interface SavedCourseRepository {
    /** [cursor] 가 null 이면 첫 페이지, 아니면 그 커서 다음부터 이어 받는다. */
    suspend fun getSavedCourses(
        size: Int,
        cursor: String? = null,
    ): CursorPageVO<SavedCourseVO>

    /**
     * 저장한 코스 id 집합. 목록 화면이 아니라 **다른 화면의 저장 여부 표시**에 쓴다.
     *
     * TODO-API-SPEC: 홈 피드 응답에 저장 여부가 없어 필요한 우회다.
     * 피드가 `hasSaved` 를 내려주면 이 조회와 호출부를 함께 제거한다. [wiki-needed]
     */
    suspend fun getSavedCourseIds(
        size: Int,
        cursor: String? = null,
    ): CursorPageVO<String>
}

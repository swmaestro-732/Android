package com.chillsam.courmy.course.domain

/**
 * 코스 저장/저장 취소 계약. 구현은 data 레이어이며 실패 시 예외를 throw 한다.
 * 사용자는 서버가 JWT 로 식별하므로 인자에 사용자 id 를 싣지 않는다(로그인 필수).
 *
 * 코스 상세·홈 피드 카드·저장함이 모두 쓰므로 course 모듈에 둔다
 * (의존 방향이 main → course 라 main 쪽에 두면 course 에서 쓸 수 없다).
 */
interface CourseSaveRepository {
    /** [saved] 가 true 면 저장(POST), false 면 저장 취소(DELETE). */
    suspend fun setSaved(
        courseId: Long,
        saved: Boolean,
    )
}

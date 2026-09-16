package com.chillsam.courmy.main.domain.home

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.home.HomeCourseVO

/**
 * 홈 공개 코스 피드 계약(`GET /service/v1/courses`). 구현은 data 레이어이며 실패 시 예외를 throw 한다.
 *
 * 비로그인도 조회할 수 있는 공개 엔드포인트라 세션이 없어도 호출한다.
 */
interface HomeFeedRepository {
    /**
     * 저장수 내림차순·최신순으로 랭킹된 공개 코스 한 페이지. [size] 는 서버가 1~50 으로 제한한다.
     * [cursor] 가 null 이면 첫 페이지, 아니면 그 커서 다음부터 이어 받는다.
     */
    suspend fun getCourseFeed(
        size: Int,
        cursor: String? = null,
    ): CursorPageVO<HomeCourseVO>
}

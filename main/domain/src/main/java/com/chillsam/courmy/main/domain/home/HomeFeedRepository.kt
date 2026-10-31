package com.chillsam.courmy.main.domain.home

import com.chillsam.courmy.common.entity.category.CourseTagVO
import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.home.HomeCourseVO
import com.chillsam.courmy.main.entity.home.HomeVO

/**
 * 홈 추천 코스 피드 계약(`GET /service/v2/home/recommended-courses`). 구현은 data 레이어이며 실패 시 예외를 throw 한다.
 *
 * 비로그인도 조회할 수 있는 공개 엔드포인트라 세션이 없어도 호출한다.
 */
interface HomeFeedRepository {
    /**
     * 저장수 내림차순·최신순으로 랭킹된 공개 코스 한 페이지. [size] 는 서버가 1~50 으로 제한한다.
     * [cursor] 가 null 이면 첫 페이지, 아니면 그 커서 다음부터 이어 받는다.
     * [category] 를 주면 해당 코스 태그의 공개 코스만 내려온다(정렬·커서 규칙은 동일). null 이면 전체.
     */
    suspend fun getCourseFeed(
        size: Int,
        cursor: String? = null,
        category: CourseTagVO? = null,
    ): CursorPageVO<HomeCourseVO>

    /**
     * 홈 화면 조합(`GET /service/v2/home`) — 추천 코스 미리보기 + 근처 저장 장소.
     *
     * 공개 엔드포인트지만 근처 저장 장소는 JWT 로 사용자를 식별해 내려주므로 비로그인이면 비어 있다.
     * [userLat]/[userLng] 는 근처 계산 기준점이며 없으면 서버 기본값을 따른다.
     */
    suspend fun getHome(
        userLat: Double? = null,
        userLng: Double? = null,
    ): HomeVO
}

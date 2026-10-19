package com.chillsam.courmy.main.domain.saved

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.saved.SavedPlaceVO

/**
 * 저장함 · 장소 탭 **조회** 계약. 구현은 data 레이어이며 실패 시 예외를 throw 한다.
 * 사용자는 서버가 JWT 로 식별하므로 인자에 id 를 싣지 않는다(로그인 필수).
 *
 * 저장/취소·방문 처리는 장소 상세 시트와 같은 동작이라
 * [com.chillsam.courmy.course.domain.PlaceSaveRepository] 가 담당한다(코스 탭과 같은 역할 분담).
 */
interface SavedPlaceRepository {
    /**
     * [cursor] 가 null 이면 첫 페이지, 아니면 그 커서 다음부터 이어 받는다.
     * [visited] 가 null 이면 전체, true/false 면 방문 여부로 거른다(필터 UI 가 생기면 쓴다).
     */
    suspend fun getSavedPlaces(
        size: Int,
        cursor: String? = null,
        visited: Boolean? = null,
    ): CursorPageVO<SavedPlaceVO>
}

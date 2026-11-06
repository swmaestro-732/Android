package com.chillsam.courmy.main.domain.search

/**
 * 최근 검색어 저장소. 기기 로컬에만 둔다(서버 계약 없음).
 * 순서가 의미(최신이 앞)라 목록 전체를 통째로 읽고 쓴다 — 조작(추가·삭제·비우기)은 호출부가 한다.
 */
interface RecentSearchRepository {
    fun getAll(): List<String>

    fun replaceAll(keywords: List<String>)
}

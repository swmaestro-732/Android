package com.chillsam.courmy.main.entity.search

/**
 * "지금 뜨는 검색어" 한 줄(FS-17).
 *
 * [change] 는 직전 집계 대비 순위 변동이다. 몇 계단 올랐는지까지 보여줘야 해서 방향만이 아니라
 * 폭([RankChange.Up.places])도 함께 들고 있는다.
 */
data class TrendingKeywordVO(
    val rank: Int,
    val keyword: String,
    val change: RankChange = RankChange.Same,
)

/** 급상승 검색어의 순위 변동. */
sealed interface RankChange {
    data class Up(
        val places: Int,
    ) : RankChange

    data class Down(
        val places: Int,
    ) : RankChange

    /** 이번 집계에 처음 올라온 검색어. 순위 폭 대신 NEW 배지를 단다. */
    data object New : RankChange

    /** 변동 없음. 시안에서는 `—` 로 표시한다. */
    data object Same : RankChange
}

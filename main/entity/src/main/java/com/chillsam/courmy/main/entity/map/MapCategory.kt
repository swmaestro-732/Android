package com.chillsam.courmy.main.entity.map

/**
 * 지도 탭 상단의 고정 카테고리 필터(FS-10).
 *
 * 검색 결과 화면(FS-10-ResultMap)의 칩은 검색어에 따라 달라지는 세분화 라벨이라 여기 넣지 않는다.
 * 그쪽은 서버가 주는 문자열을 그대로 쓴다.
 */
enum class MapCategory {
    ALL,
    CAFE,
    RESTAURANT,
    BAR,
    SHOPPING,
    CULTURE,
    EXPERIENCE,
    NATURE,
    LANDMARK,
    ENTERTAINMENT,
    WELLNESS,
}

/** 지도 하단 시트의 정렬 기준. */
enum class MapSortOrder {
    /** 저장(북마크)이 많은 순. FS-10 기본값. */
    SAVED,

    /** 가까운 순. FS-10-List 기본값. */
    DISTANCE,

    RATING,
}

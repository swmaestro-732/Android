package com.chillsam.courmy.main.entity.map

/**
 * 지도 위 고정 카테고리 필터(FS-10). 지도 탭과 지도 검색 결과(FS-10-ResultMap)가 같은 칩을 쓰고,
 * 서버 요청은 전체(ALL)를 뺀 대문자 enum 이름이다(`GET /api/v1/places/map` 의 `category`).
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

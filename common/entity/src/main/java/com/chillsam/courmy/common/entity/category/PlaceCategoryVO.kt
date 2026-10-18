package com.chillsam.courmy.common.entity.category

/** 장소·코스 테마·관심 테마(좋아하는 곳)가 공유하는 10종. 정본은 `.ai/taxonomy.md`. */
enum class PlaceCategoryVO(
    val code: String,
    val label: String,
) {
    CAFE("cafe", "카페·디저트"),
    RESTAURANT("restaurant", "음식점"),
    BAR("bar", "술집·바"),
    SHOPPING("shopping", "쇼핑·상점"),
    CULTURE("culture", "문화·전시"),
    EXPERIENCE("experience", "체험·클래스"),
    NATURE("nature", "자연·아웃도어"),
    LANDMARK("landmark", "역사·명소"),
    ENTERTAINMENT("entertainment", "여가·놀거리"),
    WELLNESS("wellness", "웰니스·힐링"),
    ;

    companion object {
        /** 대소문자 응답·캐시를 모두 읽는다. 요청 형식은 엔드포인트별 계약을 따른다. */
        fun fromCode(code: String?): PlaceCategoryVO? =
            entries.firstOrNull { it.code.equals(code?.trim(), ignoreCase = true) }

        fun labelOf(code: String?): String = fromCode(code)?.label.orEmpty()
    }
}

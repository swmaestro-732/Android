package com.chillsam.courmy.common.entity.category

/**
 * 코스 태그(코스 테마) 12종. 정본은 `.ai/taxonomy.md`.
 *
 * 장소 카테고리([PlaceCategoryVO] 10종)와는 **별개 체계**다 — 홈 테마·추천 코스 필터·관심 테마·
 * 코스 카드 좌상단은 이 12종을, 지도 필터·장소 분류는 장소 카테고리 10종을 쓴다.
 *
 * 표시·저장용 코드는 소문자([code])이고, 서버의 `likeThemes` 요청과 `theme` 응답은
 * CourseCategory enum **이름**(대문자 = [name])을 쓴다.
 */
enum class CourseTagVO(
    val code: String,
    val label: String,
) {
    DATE("date", "데이트"),
    HEALING("healing", "힐링"),
    FOOD("food", "맛집"),
    CAFETOUR("cafetour", "카페 투어"),
    CULTURE("culture", "문화"),
    NATURE("nature", "자연"),
    NIGHTVIEW("nightview", "야경"),
    SHOPPING("shopping", "쇼핑"),
    TRADITION("tradition", "역사"),
    ACTIVITY("activity", "액티비티"),
    FAMILY("family", "가족 나들이"),
    SOLO("solo", "혼놀"),
    ;

    companion object {
        /** 소문자 코드와 서버 응답의 대문자 enum 이름을 모두 읽는다. */
        fun fromCode(code: String?): CourseTagVO? =
            entries.firstOrNull { it.code.equals(code?.trim(), ignoreCase = true) }

        /**
         * 코드·enum 이름에 더해 한글 라벨("데이트")도 읽는다. 네비게이션 인자처럼
         * 과거에 라벨을 실어 보내던 느슨한 경계에서만 쓴다 — 서버 값 파싱은 [fromCode]로 충분하다.
         */
        fun fromCodeOrLabel(value: String?): CourseTagVO? =
            fromCode(value) ?: entries.firstOrNull { it.label == value?.trim() }

        fun labelOf(code: String?): String = fromCode(code)?.label.orEmpty()
    }
}

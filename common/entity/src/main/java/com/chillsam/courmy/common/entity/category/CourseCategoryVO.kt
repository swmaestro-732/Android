package com.chillsam.courmy.common.entity.category

/**
 * 코스 카테고리. 서버 `CourseCategory` enum 과 이름이 1:1 로 대응한다.
 *
 * 두 곳에서 쓰인다.
 * - **표시**: 홈 피드·저장함·코스 상세의 카테고리 칩 라벨
 * - **전송**: 관심 테마(`SignupRequest.likeThemes`, `UpdateProfileRequest.likeThemes`)
 *
 * 전송에는 반드시 [name](코드)을 쓴다 — 서버가 이 enum 으로 검증해 모르는 값은
 * `400 "존재하지 않는 관심 테마가 포함되어 있습니다"` 로 거부한다. [label] 은 화면 표시 전용이다.
 *
 * 라벨은 칩 한 줄에 들어가야 해 정본 표(`.ai/taxonomy.md` "코스 태그")보다 짧게 줄여 쓴다
 * (예: "힐링·산책" → "힐링").
 */
enum class CourseCategoryVO(
    val label: String,
) {
    DATE("데이트"),
    HEALING("힐링"),
    FOOD("맛집"),
    CAFETOUR("카페투어"),
    CULTURE("문화·전시"),
    NATURE("자연"),
    NIGHTVIEW("야경"),
    SHOPPING("쇼핑"),
    TRADITION("전통"),
    ACTIVITY("액티비티"),
    FAMILY("가족"),
    SOLO("혼자"),
    ;

    companion object {
        /** 서버 코드 → enum. 모르는 값(서버에 카테고리가 추가된 경우)이나 null 은 null. */
        fun fromCode(code: String?): CourseCategoryVO? {
            val normalized = code?.trim()?.uppercase() ?: return null
            return entries.firstOrNull { it.name == normalized }
        }

        /** 서버 코드 → 화면 라벨. 모르는 값은 빈 문자열 — 호출부가 칩을 렌더하지 않는다. */
        fun labelOf(code: String?): String = fromCode(code)?.label.orEmpty()
    }
}

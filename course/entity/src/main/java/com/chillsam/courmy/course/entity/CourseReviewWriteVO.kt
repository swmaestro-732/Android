package com.chillsam.courmy.course.entity

/**
 * 후기 작성 입력 제한. 서버 `CreateCourseReviewRequest` 의 검증 애노테이션과 **같은 값**이다.
 *
 * 여기서 먼저 막는 이유는 초과분이 400(VALIDATION_FAILED)으로만 돌아와 사용자가 어디가 문제인지
 * 알 수 없기 때문이다. 서버 값이 바뀌면 여기도 함께 고쳐야 한다.
 */
object CourseReviewLimits {
    /** 별점은 1~5 만 받는다(`@Min(1) @Max(5)`). 0 은 "아직 안 고름" 이라 전송 대상이 아니다. */
    const val MIN_RATING: Int = 1
    const val MAX_RATING: Int = 5

    /** 본문 최대 길이(`@Size(max = 1000)`). 비워 두고 별점만 남길 수도 있다. */
    const val MAX_CONTENT_LENGTH: Int = 1000

    /** 사진 최대 장수(`@Size(max = 6)`). */
    const val MAX_PHOTOS: Int = 6

    /** 태그 최대 개수(`@Size(max = 5)`). */
    const val MAX_TAGS: Int = 5
}

/**
 * 후기 태그 목록.
 *
 * 서버 `CourseReviewTag` enum 을 그대로 옮긴 것이다. **태그 목록을 내려주는 API 가 없어서**
 * 앱이 카탈로그를 들고 있어야 작성 화면에서 고를 수 있다
 * (`GET /api/v1/recommended-tags` 는 코스 테마 추천이라 이것과 무관하다).
 *
 * [code] 는 서버 enum 이름의 소문자이며 작성 요청의 `tagCodes` 에 싣는 값이다.
 * 서버가 모르는 code 를 받으면 400(INVALID_INPUT)으로 거절하므로 임의로 늘리지 않는다.
 *
 * 읽기 응답은 서버가 label·icon 을 함께 주므로 이 카탈로그를 거치지 않는다.
 * 여기 없는 code 가 응답에 있어도 화면은 서버가 준 표기를 그대로 그린다.
 */
enum class CourseReviewTagCatalog(
    val code: String,
    val label: String,
    val icon: String,
) {
    // ── 코스 구성 ──
    PACKED("packed", "구성이 알차요", "📦"),
    COMBO("combo", "장소 조합이 좋아요", "🌿"),
    SMOOTH("smooth", "흐름이 자연스러워요", "🌊"),
    EFFICIENT("efficient", "동선이 효율적이에요", "🎢"),

    // ── 이동·접근 ──
    WALKABLE("walkable", "도보로 다니기 좋아요", "👣"),
    TRANSIT("transit", "대중교통으로 편해요", "🚌"),
    BYCAR("bycar", "차 있으면 편해요", "🚗"),
    COMPACT("compact", "이동 거리가 짧아요", "📏"),
    STEEP("steep", "언덕·계단이 많아요", "🚶‍♂️"),

    // ── 시간·페이스 ──
    HALFDAY("halfday", "반나절 코스로 좋아요", "⏰"),
    FULLDAY("fullday", "하루 코스로 좋아요", "🌇"),
    QUICKCOURSE("quickcourse", "짧고 알차요", "⏳"),
    RELAXED("relaxed", "여유롭게 즐겨요", "😌"),
    SLOWPACE("slowpace", "천천히 걷기 좋아요", "🐢"),

    // ── 동반자 ──
    DATE("date", "데이트 코스로 좋아요", "❤️"),
    SOLO("solo", "혼자 즐기기 좋아요", "👤"),
    FRIENDS("friends", "친구랑 가기 좋아요", "👥"),
    FAMILY("family", "가족 나들이로 좋아요", "🏠"),
    KIDS("kids", "아이와 함께 좋아요", "👶🏻"),
    PARENTS("parents", "부모님과 가기 좋아요", "👒"),

    // ── 비용·만족 ──
    VALUE("value", "가성비 좋은 코스예요", "👛"),
    FREE("free", "무료로 즐길 수 있어요", "🎟️"),
    REVISIT("revisit", "또 가고 싶어요", "🔁"),
    MEMORABLE("memorable", "기억에 남아요", "🔖"),
    ;

    fun toTagVO(): CourseReviewTagVO = CourseReviewTagVO(code = code, label = label, icon = icon)
}

/**
 * 후기 작성 요청 입력값.
 *
 * [photoUrls] 는 **업로드가 끝난 공개 URL** 이어야 한다. 로컬 `content://` 를 그대로 보내면
 * 서버에 저장은 되지만 어디서도 보이지 않는 사진이 된다(코스 사진과 같은 규칙).
 */
data class CourseReviewDraftVO(
    val rating: Int,
    val content: String? = null,
    val photoUrls: List<String> = emptyList(),
    val tagCodes: List<String> = emptyList(),
)

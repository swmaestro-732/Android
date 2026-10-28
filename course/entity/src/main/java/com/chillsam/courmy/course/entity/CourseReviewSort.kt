package com.chillsam.courmy.course.entity

/**
 * 후기 전체보기 정렬 기준(FS-37).
 *
 * 서버가 `sort`·`order` 두 파라미터로 받으므로([sortKey]·[order]) 앱의 탭 하나를 둘로 풀어서 보낸다.
 * 서버 enum 은 `CourseReviewSortKey`(LATEST·RATING)와 `SortDirection`(ASC·DESC)이며 **이름이 그대로**
 * 쿼리에 실리므로 문자열이 일치해야 한다.
 *
 * 정렬은 서버가 수행한다. 받아 온 목록을 앱에서 다시 늘어놓으면 커서 페이징과 어긋난다 —
 * 2페이지를 붙일 때 앞 페이지까지 섞여 순서가 뒤집히기 때문이다.
 */
enum class CourseReviewSort(
    val label: String,
    val sortKey: String,
    val order: String,
) {
    /** 최신순. 작성일 내림차순. */
    LATEST("최신순", "LATEST", "DESC"),

    /** 높은 평점순. 같은 별점끼리는 서버가 최신순으로 맞춰 준다. */
    RATING("높은 평점", "RATING", "DESC"),
}

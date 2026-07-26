package com.chillsam.courmy.course.entity

/**
 * 코스 상세 화면(FS-11) 표시용 VO. `GET /service/v1/courses/{id}` 응답을 data 레이어에서 변환.
 * UI 가 안전하게 소비하도록 non-null 기본값을 가진다.
 */
data class CourseDetailVO(
    val id: String = "",
    val title: String = "",
    val coverImageUrl: String = "",
    val themes: List<String> = emptyList(),
    val description: String = "",
    val placeCount: Int = 0,
    val walkingMinutes: Int = 0,
    val tracingCountLabel: String = "",
    val author: CourseAuthorVO = CourseAuthorVO(),
    val places: List<CourseDetailPlaceVO> = emptyList(),
)

/** 코스 작성자 요약. */
data class CourseAuthorVO(
    val nickname: String = "",
    val handle: String = "",
    val profileImageUrl: String = "",
)

/** 상세의 장소 1건(이름·한마디·카테고리·사진·다음 장소까지 도보분). */
data class CourseDetailPlaceVO(
    val name: String = "",
    val caption: String = "",
    val categories: List<String> = emptyList(),
    val imageUrls: List<String> = emptyList(),
    val walkingMinutesToNext: Int? = null,
)

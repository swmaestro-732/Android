package com.chillsam.courmy.course.data.dto

import com.chillsam.courmy.course.entity.CourseAuthorVO
import com.chillsam.courmy.course.entity.CourseDetailPlaceVO
import com.chillsam.courmy.course.entity.CourseDetailVO
import kotlinx.serialization.Serializable

/**
 * `GET /service/v1/courses/{id}` 응답 DTO. 서버 필드 누락에 방어적으로 기본값을 둔다.
 * (reviewSummary 등 화면에 아직 안 쓰는 필드는 매핑하지 않는다.)
 */
@Serializable
data class CourseDetailScreenDto(
    val course: CourseDto? = null,
)

@Serializable
data class CourseDto(
    val id: String = "",
    val title: String = "",
    val coverImageUrl: String = "",
    val themes: List<String> = emptyList(),
    val description: String = "",
    val stats: CourseStatsDto = CourseStatsDto(),
    val author: CourseAuthorDto = CourseAuthorDto(),
    val places: List<CoursePlaceDto> = emptyList(),
)

@Serializable
data class CourseStatsDto(
    val placeCount: Int = 0,
    val walkingMinutes: Int = 0,
    val tracingCountLabel: String = "",
)

@Serializable
data class CourseAuthorDto(
    val nickname: String = "",
    val handle: String = "",
    val profileImageUrl: String = "",
)

@Serializable
data class CoursePlaceDto(
    val name: String = "",
    val caption: String = "",
    val categories: List<String> = emptyList(),
    val images: List<CoursePlaceImageDto> = emptyList(),
    val walkingMinutesToNext: Int? = null,
)

@Serializable
data class CoursePlaceImageDto(
    val imageUrl: String = "",
)

/** DTO → VO. course 가 없으면 빈 상세로 폴백. */
fun CourseDetailScreenDto.toVO(): CourseDetailVO {
    val c = course ?: return CourseDetailVO()
    return CourseDetailVO(
        id = c.id,
        title = c.title,
        coverImageUrl = c.coverImageUrl,
        themes = c.themes,
        description = c.description,
        placeCount = c.stats.placeCount,
        walkingMinutes = c.stats.walkingMinutes,
        tracingCountLabel = c.stats.tracingCountLabel,
        author =
            CourseAuthorVO(
                nickname = c.author.nickname,
                handle = c.author.handle,
                profileImageUrl = c.author.profileImageUrl,
            ),
        places =
            c.places.map { p ->
                CourseDetailPlaceVO(
                    name = p.name,
                    caption = p.caption,
                    categories = p.categories,
                    imageUrls = p.images.map { it.imageUrl },
                    walkingMinutesToNext = p.walkingMinutesToNext,
                )
            },
    )
}

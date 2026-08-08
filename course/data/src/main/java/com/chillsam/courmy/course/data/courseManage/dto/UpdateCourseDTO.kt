package com.chillsam.courmy.course.data.courseManage.dto

import com.chillsam.courmy.course.entity.CourseEditVO
import kotlinx.serialization.Serializable

/**
 * `PATCH /api/v1/courses/{courseId}` 요청.
 *
 * PATCH 지만 서버는 **전체 본문**을 요구한다. `thumbnailUrl`·`visibility`·`isPublished` 를 빼고 보냈더니
 * 400 `code 4001 "요청 본문 형식이 올바르지 않습니다"` 가 났다. 그래서 편집 대상이 아닌 값도 전부 싣는다.
 *
 * [isPublished] 의 이름 주의: 배포된 swagger 는 `published` 로 그리지만 그건 springdoc 이 boolean getter 의
 * `is` 를 떼서 표시하는 것이고, 실제 역직렬화는 `isPublished` 를 기대한다(생성 요청에서 확인된 동작).
 *
 * 기본값을 두지 않은 이유: `Json { encodeDefaults = false }`(기본값) 라 기본값과 같은 값은 직렬화에서
 * 빠진다. 태그를 전부 지운 경우 `tags = []` 가 기본값과 같아 필드째 누락되고, 서버는 "변경 없음"으로 읽어
 * 태그가 안 지워진다. 그래서 항상 실어 보내도록 기본값을 없앴다.
 */
@Serializable
data class UpdateCourseRequest(
    val title: String,
    val description: String?,
    val thumbnailUrl: String?,
    val tags: List<String>,
    val visibility: String,
    val places: List<UpdateCoursePlaceRequest>,
    val isPublished: Boolean,
)

/**
 * 편집 요청에 싣는 장소 1건. `caption`(한마디)만 바뀌고 나머지는 불러온 값을 그대로 돌려보낸다.
 * 서버가 places 를 통째로 치환하므로 [imageUrls] 를 빼면 장소 사진이 지워진다.
 */
@Serializable
data class UpdateCoursePlaceRequest(
    val placeId: Long,
    val orderNo: Int,
    val caption: String?,
    val imageUrls: List<String>,
)

/** 편집 값 → 요청 변환. 빈 문자열은 null 로 보내 "지움"을 표현한다. */
fun CourseEditVO.toUpdateRequest(): UpdateCourseRequest =
    UpdateCourseRequest(
        title = title.trim(),
        description = description.trim().ifBlank { null },
        thumbnailUrl = thumbnailUrl.ifBlank { null },
        tags = tags,
        visibility = visibility.name,
        isPublished = published,
        places =
            places.map { place ->
                UpdateCoursePlaceRequest(
                    placeId = place.placeId,
                    orderNo = place.orderNo,
                    caption = place.tip.trim().ifBlank { null },
                    imageUrls = place.imageUrls,
                )
            },
    )

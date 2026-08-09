package com.chillsam.courmy.course.data.courseCreate.dto

import com.chillsam.courmy.course.entity.CourseDraftVO
import kotlinx.serialization.Serializable

/**
 * `POST /api/v1/courses` 요청.
 *
 * 작성자는 JWT 로 식별된다 — 배포된 swagger 는 `userId` 를 필수 쿼리 파라미터로 표시하지만
 * 서버 실제 시그니처는 `@CurrentUserId userId: Long` 이다(springdoc 오표기).
 */
@Serializable
data class CreateCourseRequest(
    val title: String,
    val description: String? = null,
    val thumbnailUrl: String? = null,
    val tags: List<String> = emptyList(),
    val visibility: String,
    val forkedFromId: Long? = null,
    val places: List<CreateCoursePlaceRequest> = emptyList(),
    /**
     * 발행 여부. 서버 DTO 필드명이 `isPublished` 라 그대로 맞춘다.
     *
     * 배포된 swagger 는 `published` 로 표기하지만 그건 springdoc 이 boolean getter 의 `is` 를
     * 떼서 그리는 것이고, 실제 역직렬화는 `isPublished` 를 기대한다
     * (틀리면 값이 누락돼 400 "요청 본문 형식이 올바르지 않습니다"). `isFollowing`·`isNewUser` 와 같은 유형.
     */
    val isPublished: Boolean,
)

/**
 * 코스에 담는 장소 1건.
 * [placeId] 는 서버 place id 이므로, 장소 검색(`GET /api/v1/places`)으로 고른 장소만 담을 수 있다.
 *
 * [imageUrls] 는 presign 업로드를 거친 공개 URL 이다(로컬 content:// 를 그대로 보내지 않는다).
 * **기본값을 두지 않는다** — `Json { encodeDefaults = false }`(기본값) 라 기본값과 같은 값은 직렬화에서
 * 빠지는데, 서버 `CreateCoursePlaceRequest.imageUrls` 는 기본값 없는 non-null 이라 키가 없으면
 * 본문을 못 읽고 `400 code 4001 "요청 본문 형식이 올바르지 않습니다"` 가 난다. 사진을 안 고른 장소
 * (임시저장은 0장 허용)에서 정확히 이 경우가 생긴다.
 *
 * [walkingMinutes] 는 이 장소에서 **다음 장소까지의** 도보 분이다. 서버는 도보 시간을 스스로 계산하지
 * 않고 이 값을 그대로 저장해 `stats.walkingMinutes` 합계와 장소별 안내를 만든다. 빼고 보내면 코스
 * 상세가 "도보 0분" 이 된다. 마지막 장소는 null, 걸어갈 수 없는 구간은 -1 이다.
 * (null 은 `explicitNulls = false` 라 어차피 빠지고, 서버가 nullable 이라 안전하다.)
 */
@Serializable
data class CreateCoursePlaceRequest(
    val placeId: Long,
    val orderNo: Int,
    val caption: String? = null,
    val imageUrls: List<String>,
    val walkingMinutes: Int? = null,
)

@Serializable
data class CreateCourseEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: CourseIdDTO? = null,
)

@Serializable
data class CourseIdDTO(
    val courseId: Long? = null,
)

/**
 * 초안 → 생성 요청 변환.
 *
 * 장소 id 가 숫자가 아니면(과거 스텁 잔재 등) 서버가 알 수 없는 값이라 조용히 버리는 대신 제외하고,
 * 남은 장소로 순번을 다시 매긴다. 순번은 사용자가 담은 순서를 그대로 따른다.
 *
 * 도보 분은 "이 장소 → 다음 장소" 구간 값이라 **마지막 장소는 항상 null** 로 보낸다. 중간 장소가
 * 제외되면 남은 이웃끼리의 실제 도보 시간과 달라지므로, 그 경우에도 마지막만은 비워 서버가 없는
 * 구간을 만들지 않게 한다.
 */
fun CourseDraftVO.toCreateRequest(
    thumbnailUrl: String?,
    published: Boolean,
): CreateCourseRequest =
    CreateCourseRequest(
        title = name.trim(),
        thumbnailUrl = thumbnailUrl,
        description = description.ifBlank { null },
        tags = tags,
        visibility = visibility.name,
        isPublished = published,
        places =
            places
                .mapNotNull { place -> place.id.toLongOrNull()?.let { it to place } }
                .let { kept ->
                    kept.mapIndexed { index, (placeId, place) ->
                        CreateCoursePlaceRequest(
                            placeId = placeId,
                            orderNo = index,
                            caption = place.note.ifBlank { null },
                            imageUrls = place.photoUrls,
                            walkingMinutes = place.walkingMinutes.takeIf { index < kept.lastIndex },
                        )
                    }
                },
    )

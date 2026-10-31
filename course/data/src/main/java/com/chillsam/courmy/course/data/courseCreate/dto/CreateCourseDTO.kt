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
    /**
     * 포크 원본 코스 id. 남의 코스를 포크해 만든 코스면 채워 보내고, 직접 만든 코스면 null 이다.
     * 서버는 이 값을 `courses.forked_from_id` 로 저장해 출처를 추적하며, 원본이 없으면 404 로 거절한다.
     *
     * 포크 전용 API(`POST /api/v1/courses/{courseId}/forks`)가 생긴 뒤로는 포크 생성이
     * [ForkCourseRequest] 로 나가므로, 이 필드는 실제로는 항상 null 이다(라우팅은
     * `CourseRepositoryImpl.createCourse` 참고). 서버 `CreateCourseRequest` 에 아직 남아 있어
     * 계약 호환을 위해 유지한다.
     */
    val forkedFromId: Long? = null,
    /**
     * 누구와 함께했는지(`CourseCompanionVO` enum 이름, 예: `COUPLE`). 선택 입력이라 안 고르면 null 로
     * 두고, `explicitNulls = false` 라 키 자체가 빠진다.
     *
     * 서버 `CreateCourseRequest` 에는 아직 이 필드가 없다(2026-10-31 기준) — Spring 은 모르는 키를
     * 무시하므로 미리 보내도 무해하고, 서버가 필드를 추가하면 그대로 저장되기 시작한다. [wiki-needed]
     */
    val companion: String? = null,
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

/**
 * `POST /api/v1/courses/{courseId}/forks` 요청(swagger `ForkCourseRequest`).
 *
 * 원본 코스는 바디가 아니라 **경로**로 지정하므로 [CreateCourseRequest] 에서 `forkedFromId` 만 뺀
 * 모양이다. 필드 뜻·직렬화 주의사항(기본값 생략, `isPublished` 표기)은 [CreateCourseRequest] 와 같다.
 * 서버 DTO 가 다르므로 모르는 키를 실어 보내지 않도록 타입을 분리해 둔다.
 */
@Serializable
data class ForkCourseRequest(
    val title: String,
    val description: String? = null,
    val thumbnailUrl: String? = null,
    val tags: List<String> = emptyList(),
    val visibility: String,
    val places: List<CreateCoursePlaceRequest> = emptyList(),
    val isPublished: Boolean,
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
 * 생성 요청 → 포크 요청. 장소·순번·도보 분 매핑을 [toCreateRequest] 한 곳에만 두기 위해
 * 완성된 생성 요청에서 옮겨 담는다. 원본 코스 id 는 경로로 가므로 여기서 버린다.
 */
fun CreateCourseRequest.toForkRequest(): ForkCourseRequest =
    ForkCourseRequest(
        title = title,
        description = description,
        thumbnailUrl = thumbnailUrl,
        tags = tags,
        visibility = visibility,
        places = places,
        isPublished = isPublished,
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
 *
 * 포크로 시작한 초안이면 [CourseDraftVO.forkedFromId] 가 그대로 실려 서버가 출처
 * (`courses.forked_from_id`)를 남긴다. 원본이 없으면 서버가 404 로 거절한다.
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
        forkedFromId = forkedFromId,
        companion = companionType?.name,
        isPublished = published,
        places =
            places
                .withIndex()
                .mapNotNull { (sourceIndex, place) ->
                    place.id.toLongOrNull()?.let { Triple(sourceIndex, it, place) }
                }.let { kept ->
                    kept.mapIndexed { index, (sourceIndex, placeId, place) ->
                        val hasOriginalSuccessor = kept.getOrNull(index + 1)?.first == sourceIndex + 1
                        CreateCoursePlaceRequest(
                            placeId = placeId,
                            orderNo = index,
                            caption = place.note.ifBlank { null },
                            imageUrls = place.photoUrls,
                            walkingMinutes = place.walkingMinutes.takeIf { hasOriginalSuccessor },
                        )
                    }
                },
    )

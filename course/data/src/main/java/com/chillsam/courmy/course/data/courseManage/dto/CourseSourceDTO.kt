package com.chillsam.courmy.course.data.courseManage.dto

import com.chillsam.courmy.course.entity.CourseVisibility
import kotlinx.serialization.Serializable

/**
 * `GET /api/v1/courses/{courseId}`(코스 원본) 응답에서 **공개 설정만** 뽑아 쓰는 DTO.
 *
 * 편집 화면은 상세를 화면 조합 API(`/service/v1/courses/{courseId}`)로 불러오는데 거기에는
 * `visibility` 가 없다. 편집 저장은 이 값을 본문에 실어야 해서, 모른 채 PUBLIC 으로 덮어쓰지 않도록
 * 도메인 API 에서 한 번 더 읽어 온다.
 *
 * 실제 응답은 `data.course.visibility` 형태다(로그로 확인). `ignoreUnknownKeys = true` 라
 * 나머지 필드는 조용히 무시된다.
 */
@Serializable
data class CourseSourceEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: CourseSourceDTO? = null,
)

@Serializable
data class CourseSourceDTO(
    val course: CourseSourceInnerDTO? = null,
)

@Serializable
data class CourseSourceInnerDTO(
    val visibility: String? = null,
)

/** 공개 설정을 꺼낸다. 필드가 없거나 모르는 값이면 null(= 알 수 없음)로 두어 화면이 안내를 띄운다. */
fun CourseSourceEnvelope.toVisibilityOrNull(): CourseVisibility? {
    val raw = data?.course?.visibility ?: return null
    return CourseVisibility.entries.firstOrNull { it.name == raw }
}

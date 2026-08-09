package com.chillsam.courmy.course.data.draft.dto

import com.chillsam.courmy.course.data.courseCreate.dto.toCreateRequest
import com.chillsam.courmy.course.data.courseManage.dto.UpdateCoursePlaceRequest
import com.chillsam.courmy.course.data.courseManage.dto.UpdateCourseRequest
import com.chillsam.courmy.course.entity.CourseDraftVO

/**
 * 이미 만들어 둔 초안을 다시 임시저장할 때 쓰는 `PATCH /api/v1/courses/{courseId}` 요청 변환.
 *
 * 생성 요청([toCreateRequest])을 한 번 거쳐 옮겨 담는다. 장소 id 걸러내기·순번 다시 매기기·마지막
 * 장소의 도보 분 비우기 같은 규칙이 두 벌로 갈라지면 생성과 갱신의 결과가 달라지기 때문이다.
 *
 * 서버는 PATCH 여도 **전체 본문**을 요구하고 places 를 통째로 치환한다([UpdateCourseRequest] 주석 참고).
 */
fun CourseDraftVO.toDraftUpdateRequest(): UpdateCourseRequest {
    val request = toCreateRequest(thumbnailUrl = thumbnailUrl.ifBlank { null }, published = false)
    return UpdateCourseRequest(
        title = request.title,
        description = request.description,
        thumbnailUrl = request.thumbnailUrl,
        tags = request.tags,
        visibility = request.visibility,
        isPublished = request.isPublished,
        places =
            request.places.map { place ->
                UpdateCoursePlaceRequest(
                    placeId = place.placeId,
                    orderNo = place.orderNo,
                    caption = place.caption,
                    imageUrls = place.imageUrls,
                    walkingMinutes = place.walkingMinutes,
                )
            },
    )
}

package com.chillsam.courmy.course.data.draft.dto

import com.chillsam.courmy.common.entity.category.toPlaceCategoryLabel
import com.chillsam.courmy.course.data.courseDetail.dto.CoursePlaceDTO
import com.chillsam.courmy.course.data.courseDetail.dto.CourseScreenData
import com.chillsam.courmy.course.entity.CourseDraftVO
import com.chillsam.courmy.course.entity.CoursePlaceVO
import com.chillsam.courmy.course.entity.CourseVisibility

/**
 * 저장해 둔 초안 → 작성 화면 초안 VO("이어서 작성").
 *
 * 화면 조합 API(`GET /service/v1/courses/{courseId}`) 응답을 쓴다. 발행 전 코스도 그대로 내려주며,
 * 도메인 API(`GET /api/v1/courses/{courseId}`)와 달리 장소 이름·카테고리·좌표가 들어 있다.
 * 그 값이 없으면 작성 화면의 장소 카드가 이름 없는 빈 카드가 되고 도보 시간도 다시 계산할 수 없다.
 *
 * [visibility] 만은 화면 조합 응답에 없어 호출부가 도메인 API 에서 따로 읽어 넘긴다. 모르면
 * 서버 기본값과 같은 PUBLIC 으로 둔다(임시저장의 기본 공개 범위).
 */
fun CourseScreenData.toDraftVO(visibility: CourseVisibility?): CourseDraftVO {
    val course = requireNotNull(this.course) { "임시저장 코스 응답에 course 가 없습니다." }
    return CourseDraftVO(
        name = course.title.orEmpty(),
        description = course.description.orEmpty(),
        // 작성자가 직접 단 해시태그만 되돌린다. themes 는 서버가 장소 구성에서 파생한 읽기 전용 값이다.
        tags = course.tags.orEmpty(),
        places =
            course.places
                .orEmpty()
                .sortedBy { it.orderNo ?: 0 }
                .map { it.toDraftPlaceVO() },
        visibility = visibility ?: CourseVisibility.PUBLIC,
        thumbnailUrl = course.coverImageUrl.orEmpty(),
    )
}

/**
 * 장소 썸네일([CoursePlaceVO.thumbnailUrl])은 비워 둔다. 응답에 담긴 이미지는 사용자가 올린 코스
 * 사진이지 장소 대표 사진이 아니라, 그대로 끼워 넣으면 검색에서 담았을 때와 다른 그림이 된다.
 *
 * 도보 문구([CoursePlaceVO.walkText])도 비운다. 작성 화면이 초안을 불러온 직후 좌표로 다시 계산한다.
 */
private fun CoursePlaceDTO.toDraftPlaceVO(): CoursePlaceVO =
    CoursePlaceVO(
        // 작성 화면은 장소를 서버 place id 문자열로 식별한다(코스 내 식별자 id 가 아니다).
        id = (placeId ?: 0L).toString(),
        name = name.orEmpty(),
        category = categories.toPlaceCategoryLabel(),
        note = caption.orEmpty(),
        photoUrls =
            images
                .orEmpty()
                .sortedBy { it.orderNo ?: 0 }
                .mapNotNull { it.imageUrl },
        walkingMinutes = walkingMinutesToNext,
        latitude = location?.latitude,
        longitude = location?.longitude,
    )

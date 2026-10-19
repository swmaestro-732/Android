package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseDetailPlaceVO
import com.chillsam.courmy.course.entity.CourseForkSourceVO
import com.chillsam.courmy.course.entity.CoursePlaceVO
import javax.inject.Inject

/**
 * 포크할 원본 코스를 읽어 코스 만들기 화면의 초기값으로 바꾼다.
 *
 * 원본 조회는 코스 상세와 같은 경로를 쓴다 — 볼 수 없는 코스(삭제·비공개·비팔로워)는 상세와 똑같이
 * 실패하므로, 포크만 별도로 권한을 판단하지 않아도 된다.
 *
 * 가져오는 것은 **장소 구성뿐**이다. 사진·한마디는 물론 제목·설명·커버·태그·공개범위까지 포크하는
 * 사람이 새로 쓴다([CourseForkSourceVO] 참고). 서버 `ForkCourseRequest` 와 같은 계약이라,
 * 승계하지 않는 값을 여기서 채우면 저장 결과가 계약과 어긋난다.
 */
class GetCourseForkSourceUseCase
    @Inject
    constructor(
        private val repository: CourseRepository,
    ) {
        suspend operator fun invoke(courseId: Long): CourseForkSourceVO {
            val detail = repository.getCourseDetail(courseId)
            return CourseForkSourceVO(
                courseId = courseId,
                authorName = detail.authorName,
                // 서버가 순서대로 준다는 보장에 기대지 않고 표시 순번으로 다시 세운다.
                places = detail.places.sortedBy { it.order }.map(CourseDetailPlaceVO::toForkPlace),
            )
        }
    }

/**
 * 상세의 장소 → 빌더가 다룰 장소.
 *
 * [CourseDetailPlaceVO.tip]·[CourseDetailPlaceVO.imageUrls] 는 원본 작성자의 기록이라 옮기지 않는다.
 * 대신 장소를 가리키는 값(id·이름·카테고리)과 좌표·도보 분을 채워, 포크한 사람이 바로 순서를 바꾸거나
 * 장소를 더 담아도 동선과 도보 시간이 맞게 한다.
 */
private fun CourseDetailPlaceVO.toForkPlace(): CoursePlaceVO =
    CoursePlaceVO(
        id = placeId.toString(),
        name = name,
        category = category,
        walkingMinutes = walkingMinutesToNext,
        walkText = walkToNextText.orEmpty(),
        latitude = latitude,
        longitude = longitude,
    )

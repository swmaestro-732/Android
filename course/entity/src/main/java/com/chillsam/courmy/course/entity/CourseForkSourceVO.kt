package com.chillsam.courmy.course.entity

import kotlinx.serialization.Serializable

/**
 * 포크할 원본 코스에서 **빌더가 물려받는 것**.
 *
 * 포크가 원본에서 가져오는 것은 **장소 구성(어디를 어떤 순서로)** 뿐이다. 제목·설명·커버·태그·공개범위와
 * 장소별 사진·한마디는 포크하는 사람이 코스 만들기 화면에서 새로 쓴다(서버 `ForkCourseRequest` 와 같은 계약).
 * 남의 사진·팁을 그대로 퍼가는 게 아니라, 같은 코스를 자기 기록으로 다시 쓰는 기능이기 때문이다.
 *
 * - [courseId]   원본 코스 id. 생성 요청의 `forkedFromId` 로 실려 출처로 남는다
 * - [authorName] 원본 작성자 이름. "OOO님의 코스" 안내 문구에만 쓴다
 * - [places]     원본 장소 구성. 사진([CoursePlaceVO.photoUrls])과 한마디([CoursePlaceVO.note])는
 *                승계하지 않으므로 비어 있고, 장소 식별·표시·도보 계산에 필요한 값만 채워져 있다
 */
@Serializable
data class CourseForkSourceVO(
    val courseId: Long,
    val authorName: String,
    val places: List<CoursePlaceVO>,
)

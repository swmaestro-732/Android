package com.chillsam.courmy.main.entity.home

/**
 * 홈 화면(FS-09) 공개 코스 피드 카드 1건. `GET /service/v1/courses` 응답을 변환한 결과다.
 *
 * - [id]             코스 식별자(상세 이동에 사용)
 * - [title]          커버 위 코스 제목
 * - [coverImageUrl]  커버 이미지 URL(없으면 placeholder)
 * - [categoryLabel]  커버 좌상단 카테고리 칩. 서버 `theme`(CourseCategory) 을 한글로 옮긴 값
 * - [saveCountText]  저장 수 표시 텍스트(예: "342")
 *
 * TODO-API-SPEC: Figma 의 카드에는 작성자(이름·아바타), 코스 속 장소 사진·장소명, 저장 여부가 있으나
 * 피드 응답에는 `authorId` 만 있고 나머지 필드가 없다. 지금은 그 영역을 렌더하지 않는다.
 * 서버 `CourseFeedResponse.Item` 에 필드가 추가되면 VO 와 카드 UI 를 함께 되살린다. [wiki-needed]
 */
data class HomeCourseVO(
    val id: String,
    val title: String,
    val coverImageUrl: String = "",
    val categoryLabel: String = "",
    val saveCountText: String = "",
)

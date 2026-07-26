package com.chillsam.courmy.course.entity

import kotlinx.serialization.Serializable

/**
 * 작성 중인 코스 초안. Figma FS-34 CourseCreateActivity 한 화면의 전체 상태.
 *
 * - [name]          코스 이름
 * - [description]   코스 설명 (멀티라인)
 * - [tags]          선택된 태그 (칩으로 표시, 개별 삭제 가능)
 * - [suggestedTags] 추천 태그 (탭하면 [tags] 로 추가)
 * - [places]        담은 장소 목록 (드래그로 순서 변경)
 * - [visibility]    공개 범위
 */
@Serializable
data class CourseDraftVO(
    val name: String = "",
    val description: String = "",
    val tags: List<String> = emptyList(),
    val suggestedTags: List<String> = emptyList(),
    val places: List<CoursePlaceVO> = emptyList(),
    val visibility: CourseVisibility = CourseVisibility.PUBLIC,
) {
    companion object {
        val empty: CourseDraftVO = CourseDraftVO()
    }
}

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
 * - [thumbnailUrl]  코스 대표 사진. 임시저장이 서버에 남게 되면서 초안도 커버를 들고 있어야
 *                   이어서 작성할 때 되살아난다. 저장 전에는 로컬 URI(content://), 서버에서 되읽으면 공개 URL
 * - [forkedFromId]  이 초안이 남의 코스를 포크해 시작한 것이면 그 원본 코스 id. 생성 요청에 실려
 *                   서버가 `courses.forked_from_id` 로 출처를 남긴다. 직접 만든 코스는 null
 */
@Serializable
data class CourseDraftVO(
    val name: String = "",
    val description: String = "",
    val tags: List<String> = emptyList(),
    val suggestedTags: List<String> = emptyList(),
    val places: List<CoursePlaceVO> = emptyList(),
    val visibility: CourseVisibility = CourseVisibility.PUBLIC,
    val thumbnailUrl: String = "",
    val forkedFromId: Long? = null,
) {
    companion object {
        val empty: CourseDraftVO = CourseDraftVO()
    }
}

package com.chillsam.courmy.course.entity

import kotlinx.serialization.Serializable

/**
 * 저장 폴더 1개(`GET /api/v1/folders`).
 *
 * 코스 저장(`SaveCourseRequest.folderId`)에서 분류 대상으로 쓰인다. 이름은 서버가 10자 이내로
 * 제한한다([NAME_MAX_LENGTH]).
 */
@Serializable
data class CourseFolderVO(
    val id: Long,
    val name: String,
) {
    companion object {
        /** 서버 `CreateCourseFolderRequest` 의 이름 길이 제한(1~10자). */
        const val NAME_MAX_LENGTH: Int = 10
    }
}

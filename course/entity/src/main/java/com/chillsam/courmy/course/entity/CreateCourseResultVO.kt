package com.chillsam.courmy.course.entity

import kotlinx.serialization.Serializable

/**
 * 코스 생성 결과.
 *
 * [imagesUploaded] 가 false 면 코스는 만들어졌지만 사진 업로드가 실패해 이미지 없이 저장된 상태다.
 * 화면은 이때 사용자에게 알려 나중에 편집으로 다시 올리도록 유도한다.
 */
@Serializable
data class CreateCourseResultVO(
    val courseId: Long,
    val imagesUploaded: Boolean,
)

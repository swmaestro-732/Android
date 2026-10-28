package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.course.entity.CourseEditPlaceVO
import com.chillsam.courmy.course.entity.CourseEditVO
import com.chillsam.courmy.course.entity.CourseVisibility
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * 코스 편집 화면 상태.
 *
 * 편집할 수 있는 값만 담는다. 이미지·장소 구성·공개 설정은 바꿀 수 없어 [places] 의 사진·순번은
 * 불러온 값을 들고 있다가 저장할 때 그대로 되돌려 보낸다([CourseEditVO] 주석 참고).
 */
data class CourseEditUIState(
    val isLoading: Boolean = true,
    val title: String = "",
    val description: String = "",
    val tags: ImmutableList<String> = persistentListOf(),
    val places: ImmutableList<CourseEditPlaceVO> = persistentListOf(),
    /** 커버 이미지. 편집 대상이 아니라 불러온 값을 저장 때 되돌려 보내려고만 들고 있다. */
    val thumbnailUrl: String = "",
    /** 공개 설정. 도메인 API(`GET /api/v1/courses/{id}`)에서 읽어 채운다. */
    val visibility: CourseVisibility = CourseVisibility.PUBLIC,
    /**
     * 위 [visibility] 가 서버에서 읽은 실제 값인지. false 면 기본값(PUBLIC)이라는 뜻이라
     * 화면이 "현재 설정을 불러오지 못했다"고 알린다.
     */
    val isVisibilityKnown: Boolean = false,
    val isSaving: Boolean = false,
    /** 저장이 끝나면 true. 화면을 닫는 신호. */
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
) : UiState {
    /** 제목이 비면 저장할 수 없다(서버 필수값). */
    val canSave: Boolean
        get() = !isLoading && !isSaving && title.isNotBlank()

    fun toEditVO(): CourseEditVO =
        CourseEditVO(
            title = title,
            description = description,
            tags = tags,
            places = places,
            thumbnailUrl = thumbnailUrl,
            visibility = visibility,
        )

    companion object {
        val empty = CourseEditUIState()
    }
}

internal fun List<CourseEditPlaceVO>.toImmutablePlaces(): ImmutableList<CourseEditPlaceVO> = toImmutableList()

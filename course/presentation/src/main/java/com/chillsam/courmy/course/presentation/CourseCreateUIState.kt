package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.course.entity.CoursePlaceVO
import com.chillsam.courmy.course.entity.CourseVisibility
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 코스 만들기 화면 상태(Figma FS-34). 컬렉션은 Compose 안정성을 위해 Immutable* 사용.
 */
data class CourseCreateUIState(
    val isLoading: Boolean = true,
    /** 현재 단계(1..[LAST_STEP]). 1 장소 고르기 · 2 장소별 기록 · 3 코스 정보 · 4 설정. */
    val step: Int = FIRST_STEP,
    val name: String = "",
    val description: String = "",
    val thumbnailPhotos: ImmutableList<String> = persistentListOf(),
    val tags: ImmutableList<String> = persistentListOf(),
    val suggestedTags: ImmutableList<String> = persistentListOf(),
    val places: ImmutableList<CoursePlaceVO> = persistentListOf(),
    val visibility: CourseVisibility = CourseVisibility.PUBLIC,
    val isSaving: Boolean = false,
    /** 서버 저장이 끝나면 채워진다. 코스 상세로 넘어가는 신호. */
    val savedCourseId: Long? = null,
    /** 코스는 만들어졌지만 사진 업로드가 실패한 경우 true. 안내만 하고 저장은 성공으로 본다. */
    val imagesMissing: Boolean = false,
    /**
     * 이 작성 세션이 서버에 만들어 둔 임시저장 초안의 코스 id.
     *
     * 이어서 작성으로 들어왔거나 한 번이라도 임시저장했으면 채워진다. 값이 있으면 임시저장이
     * 그 초안을 갱신하므로, 여러 번 눌러도 목록에 초안이 늘지 않는다.
     */
    val draftCourseId: Long? = null,
    val isSavingDraft: Boolean = false,
    /** 임시저장이 끝나면 true. 화면을 빠져나가는 신호. */
    val draftSaved: Boolean = false,
    val errorMessage: String? = null,
) : UiState {
    /**
     * 코스 저장 가능 최소 조건: 코스 이름 有 · 장소 [MIN_PLACES]곳 이상 · 각 장소마다 사진 1장 이상.
     * 미충족 시 저장 버튼을 비활성화한다.
     */
    val canSave: Boolean
        get() =
            name.isNotBlank() &&
                places.size >= MIN_PLACES &&
                places.all { it.photoUrls.isNotEmpty() }

    /**
     * 임시저장 가능 조건은 **장소 [MIN_PLACES]곳 이상 하나뿐**이다(서버가 그 아래를 400 으로 거절한다).
     * 제목·설명·커버·장소 사진·태그는 발행할 때만 필요하므로 여기서 보지 않는다.
     */
    val canSaveDraft: Boolean
        get() = places.size >= MIN_PLACES

    /**
     * 현재 단계를 넘어가지 못하게 막는 이유. 통과 상태면 null. 버튼 문구로 그대로 쓴다.
     *
     * 단계를 나눈 가장 큰 이유가 이것이다. 한 화면일 때는 저장 버튼이 왜 비활성인지 알 수 없어
     * (조건이 화면 곳곳에 흩어져 있다) 사용자가 직접 찾아내야 했다. 이제는 막힌 지점에서 바로 말해 준다.
     *
     * 마지막 단계에서는 앞선 단계의 조건을 모두 다시 본다. 앞 단계를 통과해야 넘어올 수 있으니
     * 보통은 걸리지 않지만, 뒤로 가서 값을 지운 경우까지 저장 직전에 걸러 낸다.
     */
    val stepBlockedReason: String?
        get() =
            when (step) {
                STEP_PLACES -> placesReason
                STEP_PLACE_RECORDS -> recordsReason
                STEP_COURSE_INFO -> infoReason
                else -> placesReason ?: recordsReason ?: infoReason
            }

    /** 다음 단계로 갈 수 있는지. 마지막 단계에서는 저장 가능 여부와 같다. */
    val canGoNext: Boolean
        get() = stepBlockedReason == null

    private val placesReason: String?
        get() = if (places.size < MIN_PLACES) "장소를 ${MIN_PLACES}곳 이상 담아 주세요." else null

    private val recordsReason: String?
        get() = if (places.any { it.photoUrls.isEmpty() }) "사진을 추가해주세요" else null

    private val infoReason: String?
        get() = if (name.isBlank()) "코스 이름을 입력해 주세요." else null

    companion object {
        val empty: CourseCreateUIState = CourseCreateUIState()

        /** 썸네일(코스 대표 사진)은 1장만 선택한다. */
        const val MAX_THUMBNAIL_PHOTOS = 1

        /** 코스 저장에 필요한 최소 장소 수. */
        const val MIN_PLACES = 2

        /** 코스에 담을 수 있는 최대 장소 수. */
        const val MAX_PLACES = 10

        const val STEP_PLACES = 1
        const val STEP_PLACE_RECORDS = 2
        const val STEP_COURSE_INFO = 3
        const val STEP_SETTINGS = 4

        const val FIRST_STEP = STEP_PLACES
        const val LAST_STEP = STEP_SETTINGS
    }
}

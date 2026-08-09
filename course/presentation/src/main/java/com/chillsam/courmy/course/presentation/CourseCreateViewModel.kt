package com.chillsam.courmy.course.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.course.domain.CompleteCourseUseCase
import com.chillsam.courmy.course.domain.CreateCourseUseCase
import com.chillsam.courmy.course.domain.GetCourseDraftUseCase
import com.chillsam.courmy.course.domain.GetRecommendedTagsUseCase
import com.chillsam.courmy.course.domain.GetWalkingMinutesUseCase
import com.chillsam.courmy.course.domain.SaveDraftUseCase
import com.chillsam.courmy.course.entity.CourseCompleteVO
import com.chillsam.courmy.course.entity.CourseDraftVO
import com.chillsam.courmy.course.entity.CoursePlaceCoordinate
import com.chillsam.courmy.course.entity.CoursePlaceVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseCreateViewModel
    @Inject
    constructor(
        private val getCourseDraftUseCase: GetCourseDraftUseCase,
        private val saveDraftUseCase: SaveDraftUseCase,
        private val completeCourseUseCase: CompleteCourseUseCase,
        private val createCourseUseCase: CreateCourseUseCase,
        private val getRecommendedTagsUseCase: GetRecommendedTagsUseCase,
        private val getWalkingMinutesUseCase: GetWalkingMinutesUseCase,
    ) : MviViewModel<CourseCreateIntent, CourseCreateUIState, CourseCreateReducerEvent>(
            CourseCreateUIState.empty,
        ) {
        private var saveJob: Job? = null
        private var tagsJob: Job? = null
        private var walkJob: Job? = null

        // 플랫한 MVI 인텐트 디스패치라 분기 수만큼 길이·복잡도가 늘지만 로직 복잡도는 아니다.
        @Suppress("LongMethod", "CyclomaticComplexMethod")
        override fun onIntent(intent: CourseCreateIntent) {
            when (intent) {
                CourseCreateIntent.Load -> {
                    load()
                }

                is CourseCreateIntent.ChangeName -> {
                    dispatch(CourseCreateReducerEvent.NameChanged(intent.name))
                }

                is CourseCreateIntent.ChangeDescription -> {
                    dispatch(CourseCreateReducerEvent.DescriptionChanged(intent.description))
                }

                is CourseCreateIntent.ChangeThumbnailPhotos -> {
                    dispatch(
                        CourseCreateReducerEvent.ThumbnailPhotosChanged(
                            intent.photoUrls.take(CourseCreateUIState.MAX_THUMBNAIL_PHOTOS),
                        ),
                    )
                }

                is CourseCreateIntent.AddTag -> {
                    addTag(intent.tag)
                }

                is CourseCreateIntent.RemoveTag -> {
                    dispatch(CourseCreateReducerEvent.TagsChanged(currentState.tags - intent.tag))
                }

                is CourseCreateIntent.AddPlaces -> {
                    addPlaces(intent.places)
                }

                is CourseCreateIntent.ChangePlaceNote -> {
                    dispatch(
                        CourseCreateReducerEvent.PlacesChanged(
                            currentState.places.map { place ->
                                if (place.id == intent.placeId) place.copy(note = intent.note) else place
                            },
                        ),
                    )
                }

                is CourseCreateIntent.ChangePlacePhotos -> {
                    dispatch(
                        CourseCreateReducerEvent.PlacesChanged(
                            currentState.places.map { place ->
                                if (place.id == intent.placeId) {
                                    place.copy(photoUrls = intent.photoUrls.take(place.maxPhotos))
                                } else {
                                    place
                                }
                            },
                        ),
                    )
                }

                is CourseCreateIntent.RemovePlace -> {
                    // 장소가 빠지면 앞뒤 구간이 새로 이어지므로 도보 시간을 다시 구한다.
                    dispatch(
                        CourseCreateReducerEvent.PlacesChanged(
                            currentState.places.filterNot { it.id == intent.placeId },
                        ),
                    )
                    refreshWalkingMinutes()
                }

                is CourseCreateIntent.MovePlace -> {
                    movePlace(intent.fromIndex, intent.toIndex)
                }

                is CourseCreateIntent.ChangeVisibility -> {
                    dispatch(CourseCreateReducerEvent.VisibilityChanged(intent.visibility))
                }

                CourseCreateIntent.SaveDraft -> {
                    saveDraftUseCase(currentState.toDraftVO())
                }

                is CourseCreateIntent.CompleteCourse -> {
                    createCourse(intent.course)
                }

                CourseCreateIntent.ConsumeSaveError -> {
                    dispatch(CourseCreateReducerEvent.SaveErrorConsumed)
                }

                CourseCreateIntent.ConsumeSaved -> {
                    dispatch(CourseCreateReducerEvent.SavedConsumed)
                }

                CourseCreateIntent.NextStep -> {
                    // 조건을 못 채웠으면 넘어가지 않는다(버튼도 비활성이지만 상태 쪽에서도 막는다).
                    if (currentState.canGoNext && currentState.step < CourseCreateUIState.LAST_STEP) {
                        val next = currentState.step + 1
                        dispatch(CourseCreateReducerEvent.StepChanged(next))
                        // 장소를 다 담고 넘어가는 시점에 한 번만 구간 도보 시간을 구한다
                        // (장소를 담을 때마다 부르면 같은 계산을 여러 번 하게 된다).
                        if (next == CourseCreateUIState.STEP_PLACE_RECORDS) refreshWalkingMinutes()
                        // 태그 단계에 들어설 때, 확정된 장소들로 추천을 받는다.
                        if (next == CourseCreateUIState.LAST_STEP) loadRecommendedTags()
                    }
                }

                CourseCreateIntent.PrevStep -> {
                    if (currentState.step > CourseCreateUIState.FIRST_STEP) {
                        dispatch(CourseCreateReducerEvent.StepChanged(currentState.step - 1))
                    }
                }
            }
        }

        // onIntent 와 같은 이유: 이벤트 종류만큼 갈래가 늘 뿐, 각 갈래는 copy 한 줄이다.
        @Suppress("CyclomaticComplexMethod", "LongMethod")
        override fun reduce(
            state: CourseCreateUIState,
            event: CourseCreateReducerEvent,
        ): CourseCreateUIState =
            when (event) {
                is CourseCreateReducerEvent.LoadFailed -> {
                    state.copy(isLoading = false, errorMessage = event.message)
                }

                CourseCreateReducerEvent.LoadStarted -> {
                    state.copy(isLoading = true)
                }

                is CourseCreateReducerEvent.DraftLoaded -> {
                    // state.copy 가 아니라 **빈 상태에서 새로 만든다**. 이 화면의 엔트리가 백스택에
                    // 남아 ViewModel 이 재사용되더라도, 진입할 때마다 이전 코스의 입력(대표 사진,
                    // 저장 결과 등)이 남지 않도록 하기 위해서다. 이어서 쓸 값은 임시저장 목록에서
                    // 불러오는 것이 이 화면의 계약이다.
                    CourseCreateUIState.empty.copy(
                        isLoading = false,
                        name = event.draft.name,
                        description = event.draft.description,
                        tags = event.draft.tags.toImmutableList(),
                        suggestedTags = event.draft.suggestedTags.toImmutableList(),
                        places = event.draft.places.toImmutableList(),
                        visibility = event.draft.visibility,
                    )
                }

                is CourseCreateReducerEvent.NameChanged -> {
                    state.copy(name = event.name)
                }

                is CourseCreateReducerEvent.DescriptionChanged -> {
                    state.copy(description = event.description)
                }

                is CourseCreateReducerEvent.ThumbnailPhotosChanged -> {
                    state.copy(thumbnailPhotos = event.photoUrls.toImmutableList())
                }

                is CourseCreateReducerEvent.TagsChanged -> {
                    state.copy(tags = event.tags.toImmutableList())
                }

                is CourseCreateReducerEvent.PlacesChanged -> {
                    state.copy(places = event.places.toImmutableList())
                }

                is CourseCreateReducerEvent.VisibilityChanged -> {
                    state.copy(visibility = event.visibility)
                }

                CourseCreateReducerEvent.SaveStarted -> {
                    state.copy(isSaving = true, errorMessage = null)
                }

                is CourseCreateReducerEvent.SaveSucceeded -> {
                    state.copy(
                        isSaving = false,
                        savedCourseId = event.courseId,
                        imagesMissing = !event.imagesUploaded,
                    )
                }

                is CourseCreateReducerEvent.SaveFailed -> {
                    state.copy(isSaving = false, errorMessage = event.message)
                }

                CourseCreateReducerEvent.SaveErrorConsumed -> {
                    state.copy(errorMessage = null)
                }

                CourseCreateReducerEvent.SavedConsumed -> {
                    state.copy(savedCourseId = null, imagesMissing = false)
                }

                is CourseCreateReducerEvent.StepChanged -> {
                    state.copy(step = event.step)
                }

                is CourseCreateReducerEvent.SuggestedTagsLoaded -> {
                    state.copy(suggestedTags = event.tags.toImmutableList())
                }
            }

        private fun addTag(tag: String) {
            val trimmed = tag.trim()
            if (trimmed.isEmpty() || currentState.tags.contains(trimmed)) return
            dispatch(CourseCreateReducerEvent.TagsChanged(currentState.tags + trimmed))
        }

        private fun movePlace(
            fromIndex: Int,
            toIndex: Int,
        ) {
            val places = currentState.places
            if (fromIndex !in places.indices || toIndex !in places.indices || fromIndex == toIndex) return
            val reordered =
                places.toMutableList().apply {
                    add(toIndex, removeAt(fromIndex))
                }
            dispatch(CourseCreateReducerEvent.PlacesChanged(reordered))
            refreshWalkingMinutes()
        }

        /**
         * 장소 사이 도보 시간을 다시 구해 [CoursePlaceVO.walkText] 에 채운다.
         *
         * 좌표가 빠진 장소가 섞이면 구간을 짝지을 수 없어 아예 표시하지 않는다. 응답이 와도 구간 수가
         * 장소 수-1 과 다르면 어느 구간의 값인지 알 수 없으므로 같은 이유로 걷어낸다.
         * 실패해도 코스 만들기 자체는 계속돼야 하므로 화면을 막거나 에러를 띄우지 않는다.
         */
        private fun refreshWalkingMinutes() {
            walkJob?.cancel()
            val places = currentState.places
            if (places.size < MIN_WALK_POINTS || places.any { !it.hasLocation }) {
                places.clearedWalkTexts()?.let { dispatch(CourseCreateReducerEvent.PlacesChanged(it)) }
                return
            }
            val points =
                places.map { CoursePlaceCoordinate(latitude = it.latitude!!, longitude = it.longitude!!) }
            walkJob =
                viewModelScope.launch {
                    runCatching { getWalkingMinutesUseCase(points) }
                        .onSuccess { segments ->
                            val applied = places.withWalkTexts(segments)
                            if (applied == null) {
                                Log.w(TAG, "도보 구간 수 불일치: 장소 ${places.size}곳 / 구간 ${segments.size}개")
                                places.clearedWalkTexts()?.let { dispatch(CourseCreateReducerEvent.PlacesChanged(it)) }
                            } else {
                                dispatch(CourseCreateReducerEvent.PlacesChanged(applied))
                            }
                        }.onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "도보 시간 조회 실패: 장소 ${places.size}곳", e)
                            places.clearedWalkTexts()?.let { dispatch(CourseCreateReducerEvent.PlacesChanged(it)) }
                        }
                }
        }

        private fun addPlaces(places: List<CoursePlaceVO>) {
            val remaining = CourseCreateUIState.MAX_PLACES - currentState.places.size
            if (remaining <= 0) return
            val existingIds = currentState.places.map { it.id }.toSet()
            // 기존 상태 중복 + 입력 목록 내 중복 id 를 모두 제거한 뒤 남은 자리(최대 10곳)만큼만 담는다.
            val toAdd = places.filter { it.id !in existingIds }.distinctBy { it.id }.take(remaining)
            if (toAdd.isEmpty()) return
            dispatch(CourseCreateReducerEvent.PlacesChanged(currentState.places + toAdd))
        }

        /**
         * 초안을 서버에 코스로 생성하고(POST /api/v1/courses), 성공하면 완성 화면용 요약을 로컬에 보관한다.
         * 서버 저장이 끝나기 전에는 완성 화면으로 넘기지 않는다(실패했는데 성공처럼 보이지 않게).
         */
        private fun createCourse(completed: CourseCompleteVO?) {
            if (currentState.isSaving) return
            dispatch(CourseCreateReducerEvent.SaveStarted)
            saveJob?.cancel()
            saveJob =
                viewModelScope.launch {
                    runCatching {
                        createCourseUseCase(
                            draft = currentState.toDraftVO(),
                            thumbnailUris = currentState.thumbnailPhotos,
                            published = true,
                        )
                    }.onSuccess { result ->
                        completeCourseUseCase(completed)
                        dispatch(
                            CourseCreateReducerEvent.SaveSucceeded(
                                courseId = result.courseId,
                                imagesUploaded = result.imagesUploaded,
                            ),
                        )
                    }.onFailure { e ->
                        if (e is CancellationException) throw e
                        // 원문 예외 메시지는 로그로만 남기고, UI 에는 안정적인 문구를 노출한다.
                        Log.w(TAG, "코스 생성 실패", e)
                        dispatch(CourseCreateReducerEvent.SaveFailed("코스 저장에 실패했어요. 잠시 후 다시 시도해 주세요."))
                    }
                }
        }

        /**
         * 추천 태그를 받아 채운다.
         *
         * 태그는 저장의 필수 조건이 아니라서 실패해도 화면을 막지 않는다(추천 칩만 안 보인다).
         * 장소 id 는 서버 place id 문자열이라 Long 으로 되돌린다.
         */
        private fun loadRecommendedTags() {
            tagsJob?.cancel()
            tagsJob =
                viewModelScope.launch {
                    val placeIds = currentState.places.mapNotNull { it.id.toLongOrNull() }
                    runCatching { getRecommendedTagsUseCase(placeIds) }
                        .onSuccess { dispatch(CourseCreateReducerEvent.SuggestedTagsLoaded(it)) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "추천 태그 로드 실패", e)
                        }
                }
        }

        private fun load() {
            dispatch(CourseCreateReducerEvent.LoadStarted)
            viewModelScope.launch {
                runCatching { getCourseDraftUseCase() }
                    .onSuccess {
                        dispatch(CourseCreateReducerEvent.DraftLoaded(it))
                        // 이어 쓰는 초안에도 장소가 들어 있어 도보 시간을 채워야 한다.
                        refreshWalkingMinutes()
                    }.onFailure { e ->
                        if (e is CancellationException) throw e
                        // 스피너를 걷지 않으면 화면이 영구 정지한다(초안 없이도 작성은 가능하다).
                        Log.w(TAG, "임시저장 초안 로드 실패", e)
                        dispatch(CourseCreateReducerEvent.LoadFailed("임시저장한 내용을 불러오지 못했어요."))
                    }
            }
        }

        /** 현재 화면 입력값을 임시저장용 초안으로 변환한다. */
        private fun CourseCreateUIState.toDraftVO(): CourseDraftVO =
            CourseDraftVO(
                name = name,
                description = description,
                tags = tags,
                suggestedTags = suggestedTags,
                places = places,
                visibility = visibility,
            )

        private companion object {
            /** 도보 시간은 구간이 있어야 의미가 있다(장소 2곳부터). */
            const val MIN_WALK_POINTS = 2

            const val TAG = "CourseCreate"
        }
    }

/** 걸어서 갈 수 없는 구간(서버가 음수로 알려 준다). */
private const val UNREACHABLE_TEXT = "걸어갈 수 없는 거리"

/**
 * 구간 도보 분을 장소별 값으로 채운다. 구간 수가 장소 수-1 과 다르면 어느 구간의 값인지 알 수 없어
 * null 을 돌려 호출부가 표시를 걷어내게 한다. 마지막 장소는 갈 곳이 없어 빈 문자열·null 이다.
 * 서버 저장 요청에도 숫자 값이 필요하므로 표시 문구와 함께 원본 분을 보존한다.
 */
private fun List<CoursePlaceVO>.withWalkTexts(segments: List<Int>): List<CoursePlaceVO>? {
    if (segments.size != size - 1) return null
    return mapIndexed { index, place ->
        val minutes = segments.getOrNull(index)
        place.copy(walkText = minutes?.toWalkText().orEmpty(), walkingMinutes = minutes)
    }
}

/** 이미 비어 있으면 null 을 돌려 불필요한 상태 갱신을 막는다. */
private fun List<CoursePlaceVO>.clearedWalkTexts(): List<CoursePlaceVO>? =
    if (none { it.walkText.isNotEmpty() || it.walkingMinutes != null }) {
        null
    } else {
        map { it.copy(walkText = "", walkingMinutes = null) }
    }

/**
 * 구간 도보 분 → 화면 문구.
 *
 * 서버는 걸어서 갈 수 없는 구간(너무 멀거나 경로가 없음)을 **음수(-1)** 로 준다.
 * 이때는 분으로 환산하지 않고 그렇게 알린다.
 */
private fun Int.toWalkText(): String = if (this < 0) UNREACHABLE_TEXT else "도보 ${this}분"

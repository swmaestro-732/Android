package com.chillsam.courmy.course.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.course.domain.CompleteCourseUseCase
import com.chillsam.courmy.course.domain.CreateCourseUseCase
import com.chillsam.courmy.course.domain.GetCourseDraftUseCase
import com.chillsam.courmy.course.domain.SaveDraftUseCase
import com.chillsam.courmy.course.entity.CourseCompleteVO
import com.chillsam.courmy.course.entity.CourseDraftVO
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
    ) : MviViewModel<CourseCreateIntent, CourseCreateUIState, CourseCreateReducerEvent>(
            CourseCreateUIState.empty,
        ) {
        private var saveJob: Job? = null

        init {
            onIntent(CourseCreateIntent.Load)
        }

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
                    dispatch(
                        CourseCreateReducerEvent.PlacesChanged(
                            currentState.places.filterNot { it.id == intent.placeId },
                        ),
                    )
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
            }
        }

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
                    state.copy(
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

        private fun load() {
            dispatch(CourseCreateReducerEvent.LoadStarted)
            viewModelScope.launch {
                runCatching { getCourseDraftUseCase() }
                    .onSuccess { dispatch(CourseCreateReducerEvent.DraftLoaded(it)) }
                    .onFailure { e ->
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
            const val TAG = "CourseCreate"
        }
    }

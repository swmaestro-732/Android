package com.chillsam.courmy.course.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.course.domain.GetCourseDetailUseCase
import com.chillsam.courmy.course.domain.GetCourseVisibilityUseCase
import com.chillsam.courmy.course.domain.UpdateCourseUseCase
import com.chillsam.courmy.course.entity.CourseEditPlaceVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 코스 편집 ViewModel.
 *
 * 편집 값은 코스 상세(`GET /service/v1/courses/{courseId}`)를 다시 불러와 채운다. 상세 응답에는
 * 편집 대상이 아닌 값(사진·좌표)도 들어 있는데, 저장할 때 그대로 되돌려 보내야 지워지지 않으므로
 * 장소 사진과 순번을 상태에 들고 있는다.
 */
@HiltViewModel
class CourseEditViewModel
    @Inject
    constructor(
        private val getCourseDetailUseCase: GetCourseDetailUseCase,
        private val getCourseVisibilityUseCase: GetCourseVisibilityUseCase,
        private val updateCourseUseCase: UpdateCourseUseCase,
    ) : MviViewModel<CourseEditIntent, CourseEditUIState, CourseEditReducerEvent>(
            CourseEditUIState.empty,
        ) {
        /** 어느 코스를 편집 중인지. 화면이 라우트 인자로 넣어 준다. */
        var courseId: Long = 0L
            private set

        private var loadJob: Job? = null
        private var saveJob: Job? = null

        fun bind(courseId: Long) {
            this.courseId = courseId
        }

        override fun onIntent(intent: CourseEditIntent) {
            when (intent) {
                CourseEditIntent.Load -> {
                    load()
                }

                is CourseEditIntent.ChangeTitle -> {
                    dispatch(CourseEditReducerEvent.TitleChanged(intent.title))
                }

                is CourseEditIntent.ChangeDescription -> {
                    dispatch(CourseEditReducerEvent.DescriptionChanged(intent.description))
                }

                is CourseEditIntent.ChangeVisibility -> {
                    dispatch(CourseEditReducerEvent.VisibilityChanged(intent.visibility))
                }

                is CourseEditIntent.ChangePlaceTip -> {
                    changePlaceTip(intent.placeId, intent.tip)
                }

                is CourseEditIntent.AddTag -> {
                    addTag(intent.tag)
                }

                is CourseEditIntent.RemoveTag -> {
                    dispatch(CourseEditReducerEvent.TagsChanged(currentState.tags - intent.tag))
                }

                CourseEditIntent.Save -> {
                    save()
                }

                CourseEditIntent.DismissError -> {
                    dispatch(CourseEditReducerEvent.ErrorDismissed)
                }
            }
        }

        private fun load() {
            loadJob?.cancel()
            dispatch(CourseEditReducerEvent.LoadStarted)
            loadJob =
                viewModelScope.launch {
                    runCatching { getCourseDetailUseCase(courseId) }
                        .onSuccess { detail ->
                            dispatch(
                                CourseEditReducerEvent.Loaded(
                                    title = detail.title,
                                    description = detail.description,
                                    // 편집 대상은 작성자가 단 해시태그(tags)다. themes 는 서버가 장소
                                    // 구성에서 파생한 읽기 전용 카테고리라, 그걸 돌려보내면 사용자 태그가 지워진다.
                                    tags = detail.tags,
                                    places =
                                        detail.places.map { place ->
                                            CourseEditPlaceVO(
                                                placeId = place.placeId,
                                                // 상세 VO 의 order 는 1부터라 서버 기준(0부터)으로 되돌린다.
                                                orderNo = place.order - 1,
                                                name = place.name,
                                                tip = place.tip,
                                                imageUrls = place.imageUrls,
                                                // 편집 대상은 아니지만 빼고 보내면 서버가 지운다.
                                                walkingMinutes = place.walkingMinutesToNext,
                                            )
                                        },
                                    thumbnailUrl = detail.coverImageUrl,
                                ),
                            )
                        }.onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "코스 편집 로드 실패: courseId=$courseId", e)
                            dispatch(CourseEditReducerEvent.LoadFailed("코스를 불러오지 못했어요."))
                        }
                    loadVisibility()
                }
        }

        /**
         * 공개 설정은 화면 조합 상세 응답에 없어 도메인 API 에서 따로 읽는다.
         * 실패해도 편집 자체는 되어야 하므로 화면을 막지 않고 "알 수 없음"으로 둔다.
         */
        private suspend fun loadVisibility() {
            runCatching { getCourseVisibilityUseCase(courseId) }
                .onSuccess { dispatch(CourseEditReducerEvent.VisibilityLoaded(it)) }
                .onFailure { e ->
                    if (e is CancellationException) throw e
                    Log.w(TAG, "공개 설정 로드 실패: courseId=$courseId", e)
                    dispatch(CourseEditReducerEvent.VisibilityLoaded(null))
                }
        }

        /**
         * 태그 추가. 앞뒤 공백과 사용자가 붙인 '#' 을 떼고, 빈 값·중복은 무시한다
         * (서버로는 '#' 없는 이름만 보낸다).
         */
        private fun addTag(raw: String) {
            val tag = raw.trim().removePrefix("#").trim()
            if (tag.isBlank() || tag in currentState.tags) return
            dispatch(CourseEditReducerEvent.TagsChanged(currentState.tags + tag))
        }

        private fun changePlaceTip(
            placeId: Long,
            tip: String,
        ) {
            val updated =
                currentState.places.map { place ->
                    if (place.placeId == placeId) place.copy(tip = tip) else place
                }
            dispatch(CourseEditReducerEvent.PlacesChanged(updated))
        }

        private fun save() {
            if (!currentState.canSave) return
            saveJob?.cancel()
            val edit = currentState.toEditVO()
            dispatch(CourseEditReducerEvent.SaveStarted)
            saveJob =
                viewModelScope.launch {
                    runCatching { updateCourseUseCase(courseId, edit) }
                        .onSuccess { dispatch(CourseEditReducerEvent.Saved) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "코스 편집 저장 실패: courseId=$courseId", e)
                            dispatch(CourseEditReducerEvent.SaveFailed("저장하지 못했어요. 잠시 후 다시 시도해 주세요."))
                        }
                }
        }

        override fun reduce(
            state: CourseEditUIState,
            event: CourseEditReducerEvent,
        ): CourseEditUIState =
            when (event) {
                CourseEditReducerEvent.LoadStarted -> {
                    state.copy(isLoading = true, errorMessage = null)
                }

                is CourseEditReducerEvent.Loaded -> {
                    state.copy(
                        isLoading = false,
                        title = event.title,
                        description = event.description,
                        tags = event.tags.toImmutableList(),
                        places = event.places.toImmutableList(),
                        thumbnailUrl = event.thumbnailUrl,
                    )
                }

                is CourseEditReducerEvent.LoadFailed -> {
                    state.copy(isLoading = false, errorMessage = event.message)
                }

                is CourseEditReducerEvent.TitleChanged -> {
                    state.copy(title = event.title)
                }

                is CourseEditReducerEvent.DescriptionChanged -> {
                    state.copy(description = event.description)
                }

                is CourseEditReducerEvent.TagsChanged -> {
                    state.copy(tags = event.tags.toImmutableList())
                }

                is CourseEditReducerEvent.VisibilityLoaded -> {
                    state.copy(
                        visibility = event.visibility ?: state.visibility,
                        isVisibilityKnown = event.visibility != null,
                    )
                }

                is CourseEditReducerEvent.VisibilityChanged -> {
                    state.copy(visibility = event.visibility)
                }

                is CourseEditReducerEvent.PlacesChanged -> {
                    state.copy(places = event.places.toImmutableList())
                }

                CourseEditReducerEvent.SaveStarted -> {
                    state.copy(isSaving = true, errorMessage = null)
                }

                CourseEditReducerEvent.Saved -> {
                    state.copy(isSaving = false, isSaved = true)
                }

                is CourseEditReducerEvent.SaveFailed -> {
                    state.copy(isSaving = false, errorMessage = event.message)
                }

                CourseEditReducerEvent.ErrorDismissed -> {
                    state.copy(errorMessage = null)
                }
            }

        private companion object {
            const val TAG = "CourseEdit"
        }
    }

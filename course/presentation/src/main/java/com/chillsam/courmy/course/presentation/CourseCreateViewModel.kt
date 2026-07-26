package com.chillsam.courmy.course.presentation

import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.course.domain.CompleteCourseUseCase
import com.chillsam.courmy.course.domain.GetCourseDraftUseCase
import com.chillsam.courmy.course.domain.SaveDraftUseCase
import com.chillsam.courmy.course.entity.CourseCompleteVO
import com.chillsam.courmy.course.entity.CoursePlaceVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseCreateViewModel
    @Inject
    constructor(
        private val getCourseDraftUseCase: GetCourseDraftUseCase,
        private val saveDraftUseCase: SaveDraftUseCase,
        private val completeCourseUseCase: CompleteCourseUseCase,
    ) : MviViewModel<CourseCreateIntent, CourseCreateUIState, CourseCreateReducerEvent>(
            CourseCreateUIState.empty,
        ) {
        /** 임시저장 버튼: 현재 초안을 세션에 임시저장한다. */
        fun saveDraft(title: String) = saveDraftUseCase(title)

        /** 코스 저장 완료: 완성 데이터를 보관하고 저장 목록에 추가한다. */
        fun completeCourse(course: CourseCompleteVO?) = completeCourseUseCase(course)

        init {
            onIntent(CourseCreateIntent.Load)
        }

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
            }
        }

        override fun reduce(
            state: CourseCreateUIState,
            event: CourseCreateReducerEvent,
        ): CourseCreateUIState =
            when (event) {
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
            val existingIds = currentState.places.map { it.id }.toSet()
            val toAdd = places.filter { it.id !in existingIds }
            if (toAdd.isEmpty()) return
            dispatch(CourseCreateReducerEvent.PlacesChanged(currentState.places + toAdd))
        }

        private fun load() {
            dispatch(CourseCreateReducerEvent.LoadStarted)
            viewModelScope.launch {
                dispatch(CourseCreateReducerEvent.DraftLoaded(getCourseDraftUseCase()))
            }
        }
    }

package com.chillsam.courmy.course.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.domain.error.HttpResponseException
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.course.domain.CreateCourseReviewUseCase
import com.chillsam.courmy.course.domain.CreatePlaceReviewUseCase
import com.chillsam.courmy.course.entity.CourseReviewDraftVO
import com.chillsam.courmy.course.entity.CourseReviewLimits
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 후기 작성 ViewModel.
 *
 * 사진 업로드는 UseCase 가 제출 직전에 한 번에 처리한다. 고를 때마다 올리면 쓰다가 그만둔 사용자의
 * 사진이 S3 에 남고, 어느 리뷰에도 붙지 않아 지울 수도 없다.
 */
@HiltViewModel
class CourseReviewWriteViewModel
    @Inject
    constructor(
        private val createCourseReviewUseCase: CreateCourseReviewUseCase,
        private val createPlaceReviewUseCase: CreatePlaceReviewUseCase,
    ) : MviViewModel<CourseReviewWriteIntent, CourseReviewWriteUIState, CourseReviewWriteReducerEvent>(
            CourseReviewWriteUIState.empty,
        ) {
        private var submitJob: Job? = null

        override fun onIntent(intent: CourseReviewWriteIntent) {
            if (currentState.isSubmitting && intent != CourseReviewWriteIntent.ConsumeError) return
            when (intent) {
                is CourseReviewWriteIntent.ChangeRating -> {
                    dispatch(CourseReviewWriteReducerEvent.RatingChanged(intent.rating))
                }

                is CourseReviewWriteIntent.ChangeContent -> {
                    // 상한을 넘겨 입력하면 서버가 400 으로 거절한다. 넘치는 글자는 받지 않는다.
                    dispatch(
                        CourseReviewWriteReducerEvent.ContentChanged(
                            intent.value.take(CourseReviewLimits.MAX_CONTENT_LENGTH),
                        ),
                    )
                }

                is CourseReviewWriteIntent.ChangePhotos -> {
                    dispatch(
                        CourseReviewWriteReducerEvent.PhotosChanged(
                            intent.uris.take(CourseReviewLimits.MAX_PHOTOS),
                        ),
                    )
                }

                is CourseReviewWriteIntent.ToggleTag -> {
                    val state = currentState
                    // 상한에 닿았는데 새 태그를 고르려 하면 아무 일도 일어나지 않는다(해제는 언제나 된다).
                    val isAdding = intent.code !in state.selectedTagCodes
                    if (isAdding && !state.canSelectMoreTags) return
                    dispatch(CourseReviewWriteReducerEvent.TagToggled(intent.code))
                }

                is CourseReviewWriteIntent.Submit -> {
                    submit(intent.courseId)
                }

                is CourseReviewWriteIntent.SubmitPlace -> {
                    submit(intent.placeId, isPlace = true)
                }

                CourseReviewWriteIntent.ConsumeError -> {
                    dispatch(CourseReviewWriteReducerEvent.ErrorConsumed)
                }

                CourseReviewWriteIntent.ConsumeSubmitted -> {
                    dispatch(CourseReviewWriteReducerEvent.SubmittedConsumed)
                }
            }
        }

        private fun submit(
            courseId: Long,
            isPlace: Boolean = false,
        ) {
            val state = currentState
            // 이미 보냈거나 별점이 없으면 보내지 않는다. 중복 제출은 서버가 409 로 막지만,
            // 그 전에 사진이 한 번 더 업로드돼 쓰레기가 남는다.
            if (!state.canSubmit || state.submittedReviewId != 0L) return
            dispatch(CourseReviewWriteReducerEvent.SubmitStarted)
            submitJob?.cancel()
            submitJob =
                viewModelScope.launch {
                    runCatching {
                        val draft =
                            CourseReviewDraftVO(
                                rating = state.rating,
                                content = state.content,
                                photoUrls = state.photoUris,
                                tagCodes = state.selectedTagCodes.toList(),
                            )
                        if (isPlace) {
                            createPlaceReviewUseCase(
                                courseId,
                                draft,
                            )
                        } else {
                            createCourseReviewUseCase(courseId, draft)
                        }
                    }.onSuccess { dispatch(CourseReviewWriteReducerEvent.SubmitSucceeded(it)) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "후기 작성 실패: courseId=$courseId", e)
                            dispatch(CourseReviewWriteReducerEvent.SubmitFailed(e.toWriteMessage()))
                        }
                }
        }

        /**
         * 실패 사유를 사용자 말로 바꾼다.
         *
         * 409 는 "이미 이 코스에 리뷰를 작성했습니다" 뿐이라 그대로 안내할 수 있다(서버가 코스당 1건으로 막는다).
         * 401 은 토큰이 없거나 만료된 경우다 — "작성하지 못했어요" 로만 알리면 사용자가 로그인을 의심하지 못한다.
         */
        private fun Throwable.toWriteMessage(): String =
            when ((this as? HttpResponseException)?.rawCode) {
                CONFLICT -> "이미 후기를 남긴 곳이에요."
                UNAUTHORIZED -> "로그인이 필요해요."
                else -> message?.takeIf { this is IllegalArgumentException } ?: "후기를 등록하지 못했어요."
            }

        override fun reduce(
            state: CourseReviewWriteUIState,
            event: CourseReviewWriteReducerEvent,
        ): CourseReviewWriteUIState =
            when (event) {
                is CourseReviewWriteReducerEvent.RatingChanged -> {
                    state.copy(
                        rating = event.rating.coerceIn(0, CourseReviewLimits.MAX_RATING),
                    )
                }

                is CourseReviewWriteReducerEvent.ContentChanged -> {
                    state.copy(content = event.value)
                }

                is CourseReviewWriteReducerEvent.PhotosChanged -> {
                    state.copy(photoUris = event.uris.toImmutableList())
                }

                is CourseReviewWriteReducerEvent.TagToggled -> {
                    val next =
                        if (event.code in state.selectedTagCodes) {
                            state.selectedTagCodes - event.code
                        } else {
                            state.selectedTagCodes + event.code
                        }
                    state.copy(selectedTagCodes = next.toImmutableSet())
                }

                CourseReviewWriteReducerEvent.SubmitStarted -> {
                    state.copy(isSubmitting = true, errorMessage = null)
                }

                is CourseReviewWriteReducerEvent.SubmitSucceeded -> {
                    state.copy(isSubmitting = false, submittedReviewId = event.reviewId)
                }

                is CourseReviewWriteReducerEvent.SubmitFailed -> {
                    state.copy(isSubmitting = false, errorMessage = event.message)
                }

                CourseReviewWriteReducerEvent.ErrorConsumed -> {
                    state.copy(errorMessage = null)
                }

                CourseReviewWriteReducerEvent.SubmittedConsumed -> {
                    // 성공 표시만 지우는 게 아니라 통째로 비운다. 등록한 글이 다음 작성 화면에
                    // 그대로 남아 있으면 같은 내용을 또 보내려다 409 를 맞는다.
                    CourseReviewWriteUIState.empty
                }
            }

        private companion object {
            const val TAG = "CourseReviewWrite"
            const val UNAUTHORIZED = 401
            const val CONFLICT = 409
        }
    }

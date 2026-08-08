package com.chillsam.courmy.course.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.domain.auth.IsLoggedInUseCase
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.course.domain.DeleteCourseUseCase
import com.chillsam.courmy.course.domain.GetCourseDetailUseCase
import com.chillsam.courmy.course.domain.SetCourseSavedUseCase
import com.chillsam.courmy.course.domain.SetFollowAuthorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 코스 상세 화면 ViewModel(FS-11). [CourseDetailIntent.Load] 로 받은 코스를 BFF 상세 API 로 조회한다.
 *
 * 생성 시점에는 조회하지 않는다. 라우트 인자가 화면에서 넘어와야 코스가 정해지는데,
 * init 에서 미리 부르면 아직 값이 없는 id(0)로 요청이 한 번 나가 404 가 뜬다.
 */
@HiltViewModel
class CourseDetailViewModel
    @Inject
    constructor(
        private val getCourseDetailUseCase: GetCourseDetailUseCase,
        private val setCourseSavedUseCase: SetCourseSavedUseCase,
        private val setFollowAuthorUseCase: SetFollowAuthorUseCase,
        private val deleteCourseUseCase: DeleteCourseUseCase,
        private val isLoggedInUseCase: IsLoggedInUseCase,
    ) : MviViewModel<CourseDetailIntent, CourseDetailUIState, CourseDetailReducerEvent>(
            CourseDetailUIState.empty,
        ) {
        /** 진행 중인 로드 코루틴. 재요청 시 이전 것을 취소해 중복 실행·stale 결과 반영을 막는다. */
        private var loadJob: Job? = null
        private var saveJob: Job? = null
        private var followJob: Job? = null
        private var deleteJob: Job? = null

        /** 조회 대상 코스. null 이면 아직 [CourseDetailIntent.Load] 를 받지 못한 상태다. */
        private var courseId: Long? = null

        override fun onIntent(intent: CourseDetailIntent) {
            when (intent) {
                is CourseDetailIntent.Load -> {
                    // 같은 코스로 재구성되면 다시 부르지 않는다(화면 회전·재진입).
                    if (courseId == intent.courseId) return
                    courseId = intent.courseId
                    load()
                }

                CourseDetailIntent.Retry -> {
                    load()
                }

                CourseDetailIntent.ToggleSave -> {
                    toggleSave()
                }

                CourseDetailIntent.ToggleFollowAuthor -> {
                    toggleFollowAuthor()
                }

                CourseDetailIntent.Delete -> {
                    delete()
                }

                CourseDetailIntent.ConsumeError -> {
                    dispatch(CourseDetailReducerEvent.ErrorConsumed)
                }

                CourseDetailIntent.ConsumeLoginRequired -> {
                    dispatch(CourseDetailReducerEvent.LoginRequiredConsumed)
                }
            }
        }

        override fun reduce(
            state: CourseDetailUIState,
            event: CourseDetailReducerEvent,
        ): CourseDetailUIState =
            when (event) {
                CourseDetailReducerEvent.LoadStarted -> {
                    state.copy(isLoading = true, errorMessage = null)
                }

                is CourseDetailReducerEvent.Loaded -> {
                    state.copy(isLoading = false, detail = event.detail, errorMessage = null)
                }

                is CourseDetailReducerEvent.Failed -> {
                    state.copy(isLoading = false, errorMessage = event.message)
                }

                CourseDetailReducerEvent.SaveStarted -> {
                    state.copy(isSaving = true, actionErrorMessage = null)
                }

                is CourseDetailReducerEvent.SaveFinished -> {
                    state.copy(isSaving = false, detail = state.detail?.copy(isSaved = event.saved))
                }

                is CourseDetailReducerEvent.SaveFailed -> {
                    state.copy(isSaving = false, actionErrorMessage = event.message)
                }

                is CourseDetailReducerEvent.FollowFinished -> {
                    state.copy(
                        isFollowing = false,
                        detail = state.detail?.copy(isFollowingAuthor = event.following),
                    )
                }

                CourseDetailReducerEvent.FollowStarted -> {
                    state.copy(isFollowing = true, actionErrorMessage = null)
                }

                is CourseDetailReducerEvent.FollowFailed -> {
                    state.copy(isFollowing = false, actionErrorMessage = event.message)
                }

                CourseDetailReducerEvent.ErrorConsumed -> {
                    state.copy(actionErrorMessage = null)
                }

                CourseDetailReducerEvent.LoginRequired -> {
                    state.copy(needsLogin = true)
                }

                CourseDetailReducerEvent.LoginRequiredConsumed -> {
                    state.copy(needsLogin = false)
                }

                CourseDetailReducerEvent.DeleteStarted -> {
                    state.copy(isDeleting = true, actionErrorMessage = null)
                }

                CourseDetailReducerEvent.Deleted -> {
                    state.copy(isDeleting = false, isDeleted = true)
                }

                is CourseDetailReducerEvent.DeleteFailed -> {
                    state.copy(isDeleting = false, actionErrorMessage = event.message)
                }
            }

        /**
         * 작성자 팔로우 토글. 저장과 같은 이유로 서버가 확정한 뒤에 상태를 바꾼다.
         * 작성자 id 가 없으면(서버가 주지 않은 경우) 요청 대상이 없으므로 조용히 무시한다.
         *
         * 비로그인이면 요청을 보내지 않는다 — 401 을 받고 "팔로우하지 못했어요"로 알리면
         * 왜 실패했는지 알 수 없어서, 누르기 전에 로그인 안내를 띄운다.
         */
        private fun toggleFollowAuthor() {
            val state = currentState
            val detail = state.detail ?: return
            val authorId = detail.authorId
            if (state.isFollowing || authorId <= 0L) return
            if (!isLoggedInUseCase()) {
                dispatch(CourseDetailReducerEvent.LoginRequired)
                return
            }
            val target = !detail.isFollowingAuthor
            dispatch(CourseDetailReducerEvent.FollowStarted)
            followJob?.cancel()
            followJob =
                viewModelScope.launch {
                    runCatching { setFollowAuthorUseCase(userId = authorId, follow = target) }
                        .onSuccess { dispatch(CourseDetailReducerEvent.FollowFinished(it)) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "팔로우 토글 실패: userId=$authorId, target=$target", e)
                            val message = if (target) "팔로우하지 못했어요." else "팔로우를 해제하지 못했어요."
                            dispatch(CourseDetailReducerEvent.FollowFailed(message))
                        }
                }
        }

        /** 되돌릴 수 없는 동작이라 중복 탭을 막고, 서버가 확정한 뒤에만 화면을 닫는다. */
        private fun delete() {
            val id = courseId
            if (currentState.isDeleting || id == null) return
            dispatch(CourseDetailReducerEvent.DeleteStarted)
            deleteJob?.cancel()
            deleteJob =
                viewModelScope.launch {
                    runCatching { deleteCourseUseCase(id) }
                        .onSuccess { dispatch(CourseDetailReducerEvent.Deleted) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "코스 삭제 실패: courseId=$id", e)
                            dispatch(CourseDetailReducerEvent.DeleteFailed("삭제하지 못했어요. 잠시 후 다시 시도해 주세요."))
                        }
                }
        }

        /**
         * 서버가 확정한 뒤에 상태를 바꾼다(먼저 바꾸고 실패 시 되돌리면 버튼이 튀어 보인다).
         * 진행 중 중복 탭은 무시한다.
         */
        private fun toggleSave() {
            val state = currentState
            val id = courseId
            val detail = state.detail
            if (state.isSaving || id == null || detail == null) return
            val target = !detail.isSaved
            dispatch(CourseDetailReducerEvent.SaveStarted)
            saveJob?.cancel()
            saveJob =
                viewModelScope.launch {
                    runCatching { setCourseSavedUseCase(courseId = id, saved = target) }
                        .onSuccess {
                            // 요청 중에 다른 코스로 이동했으면 그 코스의 상태를 바꾸면 안 된다.
                            if (courseId == id) dispatch(CourseDetailReducerEvent.SaveFinished(target))
                        }.onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "코스 저장 토글 실패: courseId=$id, target=$target", e)
                            val message = if (target) "저장하지 못했어요." else "저장을 취소하지 못했어요."
                            dispatch(CourseDetailReducerEvent.SaveFailed(message))
                        }
                }
        }

        private fun load() {
            // 라우트 인자가 없거나 숫자가 아니면 0 이 넘어온다. 서버에 물어볼 것도 없이 에러로 끝낸다.
            val id = courseId
            if (id == null || id <= 0L) {
                Log.w(TAG, "잘못된 코스 id: $id")
                dispatch(CourseDetailReducerEvent.Failed("코스를 찾을 수 없습니다."))
                return
            }
            dispatch(CourseDetailReducerEvent.LoadStarted)
            loadJob?.cancel()
            loadJob =
                viewModelScope.launch {
                    Log.d(TAG, "코스 상세 로드 시작: courseId=$id")
                    runCatching { getCourseDetailUseCase(id) }
                        .onSuccess { detail ->
                            Log.d(
                                TAG,
                                "로드 성공: title='${detail.title}', " +
                                    "장소 ${detail.places.size}곳, 리뷰 ${detail.reviews.size}건, " +
                                    "커버=${detail.coverImageUrl.ifBlank { "(없음)" }}",
                            )
                            dispatch(CourseDetailReducerEvent.Loaded(detail))
                        }.onFailure { e ->
                            // 협력적 취소(ViewModel clear·재요청 등)는 실패가 아니므로 그대로 전파한다.
                            if (e is CancellationException) throw e
                            // 실패 원인(타임아웃/404/파싱 등)을 스택트레이스까지 남긴다.
                            Log.w(TAG, "로드 실패: ${e.javaClass.simpleName} - ${e.message}", e)
                            dispatch(CourseDetailReducerEvent.Failed(e.message ?: "코스를 불러오지 못했습니다."))
                        }
                }
        }

        private companion object {
            const val TAG = "CourseDetail"
        }
    }

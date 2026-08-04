package com.chillsam.courmy.main.presentation.my

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.main.domain.my.CheckHandleAvailabilityUseCase
import com.chillsam.courmy.main.domain.my.GetMyProfileUseCase
import com.chillsam.courmy.main.domain.my.HandleFormatError
import com.chillsam.courmy.main.domain.my.UpdateMyProfileUseCase
import com.chillsam.courmy.main.domain.my.validateHandleFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 프로필 편집(FS-26) ViewModel.
 *
 * 편집 원본은 [GetMyProfileUseCase] 로 불러오고, 저장은 [UpdateMyProfileUseCase]
 * (`PATCH /api/v1/users`)로 **바뀐 필드만** 보낸다. 아이디는 [CheckHandleAvailabilityUseCase] 로
 * 중복 확인을 통과해야 저장할 수 있다.
 */
@HiltViewModel
class ProfileEditViewModel
    @Inject
    constructor(
        private val getMyProfileUseCase: GetMyProfileUseCase,
        private val updateMyProfileUseCase: UpdateMyProfileUseCase,
        private val checkHandleAvailabilityUseCase: CheckHandleAvailabilityUseCase,
    ) : MviViewModel<ProfileEditIntent, ProfileEditUIState, ProfileEditReducerEvent>(
            ProfileEditUIState.empty,
        ) {
        private var loadJob: Job? = null
        private var checkJob: Job? = null
        private var saveJob: Job? = null

        init {
            onIntent(ProfileEditIntent.Load)
        }

        override fun onIntent(intent: ProfileEditIntent) {
            when (intent) {
                ProfileEditIntent.Load,
                ProfileEditIntent.Retry,
                -> {
                    load()
                }

                is ProfileEditIntent.NicknameChanged -> {
                    dispatch(ProfileEditReducerEvent.NicknameChanged(intent.value))
                }

                is ProfileEditIntent.HandleChanged -> {
                    dispatch(ProfileEditReducerEvent.HandleChanged(intent.value))
                }

                is ProfileEditIntent.ImagePicked -> {
                    dispatch(ProfileEditReducerEvent.ImagePicked(intent.uri))
                }

                ProfileEditIntent.CheckHandle -> {
                    checkHandle()
                }

                ProfileEditIntent.Save -> {
                    save()
                }

                ProfileEditIntent.ConsumeError -> {
                    dispatch(ProfileEditReducerEvent.ErrorConsumed)
                }
            }
        }

        override fun reduce(
            state: ProfileEditUIState,
            event: ProfileEditReducerEvent,
        ): ProfileEditUIState =
            when (event) {
                ProfileEditReducerEvent.LoadStarted -> {
                    state.copy(isLoading = true, loadErrorMessage = null)
                }

                is ProfileEditReducerEvent.Loaded -> {
                    state.copy(
                        isLoading = false,
                        loadErrorMessage = null,
                        originalNickname = event.profile.nickname,
                        originalHandle = event.profile.handle,
                        originalImageUrl = event.profile.profileImageUrl,
                        nickname = event.profile.nickname,
                        handle = event.profile.handle,
                        pickedImageUri = null,
                        handleCheck = null,
                    )
                }

                is ProfileEditReducerEvent.LoadFailed -> {
                    state.copy(isLoading = false, loadErrorMessage = event.message)
                }

                is ProfileEditReducerEvent.NicknameChanged -> {
                    state.copy(nickname = event.value)
                }

                is ProfileEditReducerEvent.HandleChanged -> {
                    // 값이 바뀌면 이전 중복 확인 결과는 무효다.
                    state.copy(handle = event.value, handleCheck = null)
                }

                is ProfileEditReducerEvent.ImagePicked -> {
                    state.copy(pickedImageUri = event.uri)
                }

                ProfileEditReducerEvent.HandleCheckStarted -> {
                    state.copy(isCheckingHandle = true, handleCheck = null)
                }

                is ProfileEditReducerEvent.HandleChecked -> {
                    // 확인 요청을 보낸 뒤 값이 또 바뀌었으면, 이 결과는 다른 아이디의 것이라 버린다.
                    // (버리지 않으면 서버가 확인한 적 없는 값으로 저장이 열린다)
                    if (event.handle == state.handle) {
                        state.copy(isCheckingHandle = false, handleCheck = event.result)
                    } else {
                        state.copy(isCheckingHandle = false)
                    }
                }

                ProfileEditReducerEvent.SaveStarted -> {
                    state.copy(isSaving = true, errorMessage = null)
                }

                ProfileEditReducerEvent.Saved -> {
                    state.copy(isSaving = false, isSaved = true)
                }

                is ProfileEditReducerEvent.SaveFailed -> {
                    state.copy(isSaving = false, errorMessage = event.message)
                }

                ProfileEditReducerEvent.ErrorConsumed -> {
                    state.copy(errorMessage = null)
                }
            }

        private fun load() {
            dispatch(ProfileEditReducerEvent.LoadStarted)
            loadJob?.cancel()
            loadJob =
                viewModelScope.launch {
                    runCatching { getMyProfileUseCase() }
                        .onSuccess { profile -> dispatch(ProfileEditReducerEvent.Loaded(profile)) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "편집 원본 프로필 로드 실패", e)
                            dispatch(ProfileEditReducerEvent.LoadFailed("프로필을 불러오지 못했습니다."))
                        }
                }
        }

        /** 형식은 서버 왕복 없이 먼저 거르고, 중복 여부만 서버에 묻는다. */
        private fun checkHandle() {
            val handle = currentState.handle
            val formatError = validateHandleFormat(handle)
            if (formatError != null) {
                dispatch(
                    ProfileEditReducerEvent.HandleChecked(
                        handle = handle,
                        result = HandleCheckResult(formatError.toMessage(), available = false),
                    ),
                )
                return
            }
            dispatch(ProfileEditReducerEvent.HandleCheckStarted)
            checkJob?.cancel()
            checkJob =
                viewModelScope.launch {
                    runCatching { checkHandleAvailabilityUseCase(handle) }
                        .onSuccess { available ->
                            dispatch(
                                ProfileEditReducerEvent.HandleChecked(
                                    handle = handle,
                                    result =
                                        HandleCheckResult(
                                            message = if (available) "사용 가능한 아이디예요" else "이미 사용 중인 아이디예요",
                                            available = available,
                                        ),
                                ),
                            )
                        }.onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "아이디 확인 실패: handle=$handle", e)
                            dispatch(
                                ProfileEditReducerEvent.HandleChecked(
                                    handle = handle,
                                    result = HandleCheckResult("확인하지 못했어요. 잠시 후 다시 시도해 주세요", available = false),
                                ),
                            )
                        }
                }
        }

        /**
         * 바뀐 필드만 보낸다(null = 변경 안 함). 중복 탭으로 두 번 저장되지 않도록
         * 진행 중이면 무시하고, 변경이 없으면 요청 없이 닫는다.
         */
        private fun save() {
            val state = currentState
            if (state.isSaving) return
            if (!state.hasChanges) {
                dispatch(ProfileEditReducerEvent.Saved)
                return
            }
            dispatch(ProfileEditReducerEvent.SaveStarted)
            saveJob?.cancel()
            saveJob =
                viewModelScope.launch {
                    runCatching {
                        updateMyProfileUseCase(
                            nickname = state.nickname.takeIf { it != state.originalNickname },
                            handle = state.handle.takeIf { it != state.originalHandle },
                            localImageUri = state.pickedImageUri,
                        )
                    }.onSuccess { dispatch(ProfileEditReducerEvent.Saved) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "프로필 저장 실패", e)
                            dispatch(ProfileEditReducerEvent.SaveFailed("저장하지 못했어요. 잠시 후 다시 시도해 주세요."))
                        }
                }
        }

        private fun HandleFormatError.toMessage(): String =
            when (this) {
                HandleFormatError.LENGTH -> "3~12자로 입력해 주세요"
                HandleFormatError.CHARSET -> "영문 소문자·숫자·밑줄(_)만 쓸 수 있어요"
            }

        companion object {
            private const val TAG = "ProfileEditViewModel"
        }
    }

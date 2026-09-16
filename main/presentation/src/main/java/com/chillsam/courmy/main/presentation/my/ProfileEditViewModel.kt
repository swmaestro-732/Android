package com.chillsam.courmy.main.presentation.my

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.domain.profile.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProfileEditIntent : MviIntent {
    /** 바뀐 항목만 담아 저장. 값이 그대로면 null 로 넘긴다. */
    data class Save(
        val nickname: String?,
        val handle: String?,
        val localImageUri: String?,
        val bio: String?,
    ) : ProfileEditIntent

    data object ConsumeSaved : ProfileEditIntent

    data object ConsumeError : ProfileEditIntent
}

data class ProfileEditUiState(
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val errorMessage: String? = null,
) : UiState {
    companion object {
        val empty = ProfileEditUiState()
    }
}

sealed interface ProfileEditReducerEvent : ReducerEvent {
    data object Started : ProfileEditReducerEvent

    data object Succeeded : ProfileEditReducerEvent

    data class Failed(
        val message: String,
    ) : ProfileEditReducerEvent

    data object SavedConsumed : ProfileEditReducerEvent

    data object ErrorConsumed : ProfileEditReducerEvent
}

/**
 * 프로필 편집(FS-26) 저장 ViewModel.
 *
 * 실 저장은 [UpdateProfileUseCase](이미지가 있으면 presign 업로드 → `PATCH /api/v1/users`)가 담당한다.
 * 소개(bio)도 같은 요청에 실려 서버에 저장된다.
 */
@HiltViewModel
class ProfileEditViewModel
    @Inject
    constructor(
        private val updateProfileUseCase: UpdateProfileUseCase,
    ) : MviViewModel<ProfileEditIntent, ProfileEditUiState, ProfileEditReducerEvent>(
            ProfileEditUiState.empty,
        ) {
        private var saveJob: Job? = null

        override fun onIntent(intent: ProfileEditIntent) {
            when (intent) {
                is ProfileEditIntent.Save -> save(intent)
                ProfileEditIntent.ConsumeSaved -> dispatch(ProfileEditReducerEvent.SavedConsumed)
                ProfileEditIntent.ConsumeError -> dispatch(ProfileEditReducerEvent.ErrorConsumed)
            }
        }

        override fun reduce(
            state: ProfileEditUiState,
            event: ProfileEditReducerEvent,
        ): ProfileEditUiState =
            when (event) {
                ProfileEditReducerEvent.Started -> state.copy(isSaving = true, errorMessage = null)
                ProfileEditReducerEvent.Succeeded -> state.copy(isSaving = false, saved = true)
                is ProfileEditReducerEvent.Failed -> state.copy(isSaving = false, errorMessage = event.message)
                ProfileEditReducerEvent.SavedConsumed -> state.copy(saved = false)
                ProfileEditReducerEvent.ErrorConsumed -> state.copy(errorMessage = null)
            }

        private fun save(intent: ProfileEditIntent.Save) {
            if (currentState.isSaving) return
            dispatch(ProfileEditReducerEvent.Started)
            saveJob?.cancel()
            saveJob =
                viewModelScope.launch {
                    runCatching {
                        updateProfileUseCase(
                            nickname = intent.nickname,
                            handle = intent.handle,
                            localImageUri = intent.localImageUri,
                            bio = intent.bio,
                        )
                    }.onSuccess { dispatch(ProfileEditReducerEvent.Succeeded) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            // 원문 예외 메시지는 로그로만 남기고, UI 에는 안정적인 문구를 노출한다.
                            Log.w(TAG, "프로필 저장 실패", e)
                            dispatch(ProfileEditReducerEvent.Failed("저장에 실패했어요. 잠시 후 다시 시도해 주세요."))
                        }
                }
        }

        private companion object {
            const val TAG = "ProfileEdit"
        }
    }

package com.chillsam.courmy.main.presentation.login

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.domain.auth.SignupUseCase
import com.chillsam.courmy.main.entity.auth.SignupProfile
import com.chillsam.courmy.main.presentation.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SignupIntent : MviIntent {
    data class Submit(
        val profile: SignupProfile,
        /** 사용자가 고른 로컬 이미지(content://). 가입 성공 후 업로드한다. */
        val localImageUri: String? = null,
    ) : SignupIntent

    data object ConsumeDone : SignupIntent

    data object ConsumeError : SignupIntent
}

data class SignupUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val done: Boolean = false,
    /** 가입은 됐지만 프로필 사진 업로드가 실패한 경우 true. 안내만 하고 가입은 성공으로 본다. */
    val imageMissing: Boolean = false,
) : UiState {
    companion object {
        val empty = SignupUiState()
    }
}

sealed interface SignupReducerEvent : ReducerEvent {
    data object Started : SignupReducerEvent

    data class Succeeded(
        /** 프로필 사진 업로드까지 성공했는지. false 면 계정은 만들어졌지만 사진이 빠졌다. */
        val imagesUploaded: Boolean,
    ) : SignupReducerEvent

    data class Failed(
        val message: String,
    ) : SignupReducerEvent

    data object DoneConsumed : SignupReducerEvent

    data object ErrorConsumed : SignupReducerEvent
}

/** 회원가입 완료(FS-08) ViewModel. 저장된 registrationToken + 입력 프로필로 가입 API 를 호출한다. */
@HiltViewModel
class SignupViewModel
    @Inject
    constructor(
        private val signupUseCase: SignupUseCase,
    ) : MviViewModel<SignupIntent, SignupUiState, SignupReducerEvent>(SignupUiState.empty) {
        private var job: Job? = null

        override fun onIntent(intent: SignupIntent) {
            when (intent) {
                is SignupIntent.Submit -> submit(intent.profile, intent.localImageUri)
                SignupIntent.ConsumeDone -> dispatch(SignupReducerEvent.DoneConsumed)
                SignupIntent.ConsumeError -> dispatch(SignupReducerEvent.ErrorConsumed)
            }
        }

        override fun reduce(
            state: SignupUiState,
            event: SignupReducerEvent,
        ): SignupUiState =
            when (event) {
                SignupReducerEvent.Started -> {
                    state.copy(isLoading = true, errorMessage = null)
                }

                is SignupReducerEvent.Succeeded -> {
                    state.copy(isLoading = false, done = true, imageMissing = !event.imagesUploaded)
                }

                is SignupReducerEvent.Failed -> {
                    state.copy(isLoading = false, errorMessage = event.message)
                }

                SignupReducerEvent.DoneConsumed -> {
                    state.copy(done = false)
                }

                SignupReducerEvent.ErrorConsumed -> {
                    state.copy(errorMessage = null)
                }
            }

        private fun submit(
            profile: SignupProfile,
            localImageUri: String?,
        ) {
            if (currentState.isLoading) return
            dispatch(SignupReducerEvent.Started)
            job?.cancel()
            job =
                viewModelScope.launch {
                    runCatching {
                        signupUseCase(
                            profile = profile,
                            localImageUri = localImageUri,
                            fallbackImageUrl = if (BuildConfig.DEBUG) PLACEHOLDER_IMAGE_URL else null,
                        )
                    }
                        // 이미지 업로드가 실패해도(false) 계정은 만들어졌으므로 가입은 성공으로 끝낸다.
                        .onSuccess { imagesUploaded ->
                            dispatch(SignupReducerEvent.Succeeded(imagesUploaded = imagesUploaded))
                        }.onFailure { e ->
                            if (e is CancellationException) throw e
                            // 원시 예외 메시지는 로그로만 남기고 사용자에게는 고정 안내 문구를 보여준다.
                            Log.w(TAG, "회원가입 실패: ${e.javaClass.simpleName} - ${e.message}", e)
                            dispatch(SignupReducerEvent.Failed("회원가입에 실패했어요. 잠시 후 다시 시도해 주세요."))
                        }
                }
        }

        private companion object {
            const val TAG = "Signup"

            /**
             * TODO-API-SPEC: presign 이 서버 S3 미설정으로 500 인 동안 프로필 사진이 붙은 상태를
             * 확인하기 위한 임시 대체 이미지. 실제로 뜨는 이미지가 아니라 "URL 문자열이 들어갔는지"만 보는 더미다.
             * S3 가 설정되면 이 상수와 전달을 제거한다. [wiki-needed]
             */
            const val PLACEHOLDER_IMAGE_URL = "https://test.com/test.jpg"
        }
    }

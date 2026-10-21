package com.chillsam.courmy.main.presentation.login

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.domain.auth.SignupUseCase
import com.chillsam.courmy.main.entity.area.AreaVO
import com.chillsam.courmy.main.entity.auth.SignupProfile
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
        /** 가입 중 고른 관심사. 서버 전송분과 별개로 기기에도 남긴다([SignupUseCase] 주석 참고). */
        val interestThemes: List<String> = emptyList(),
        val interestRegions: List<AreaVO> = emptyList(),
    ) : SignupIntent

    data object ConsumeDone : SignupIntent

    data object ConsumeError : SignupIntent
}

data class SignupUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val done: Boolean = false,
) : UiState {
    companion object {
        val empty = SignupUiState()
    }
}

sealed interface SignupReducerEvent : ReducerEvent {
    data object Started : SignupReducerEvent

    data object Succeeded : SignupReducerEvent

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
                is SignupIntent.Submit -> submit(intent)
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

                SignupReducerEvent.Succeeded -> {
                    state.copy(isLoading = false, done = true)
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

        private fun submit(intent: SignupIntent.Submit) {
            if (currentState.isLoading) return
            dispatch(SignupReducerEvent.Started)
            job?.cancel()
            job =
                viewModelScope.launch {
                    runCatching {
                        signupUseCase(
                            profile = intent.profile,
                            localImageUri = intent.localImageUri,
                            interestThemes = intent.interestThemes,
                            interestRegions = intent.interestRegions,
                        )
                    }
                        // 이미지 업로드가 실패해도(false) 계정은 만들어졌으므로 가입은 성공으로 끝낸다.
                        .onSuccess { dispatch(SignupReducerEvent.Succeeded) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            // 원시 예외 메시지는 로그로만 남기고 사용자에게는 고정 안내 문구를 보여준다.
                            Log.w(TAG, "회원가입 실패: ${e.javaClass.simpleName} - ${e.message}", e)
                            dispatch(SignupReducerEvent.Failed("회원가입에 실패했어요. 잠시 후 다시 시도해 주세요."))
                        }
                }
        }

        private companion object {
            const val TAG = "Signup"
        }
    }

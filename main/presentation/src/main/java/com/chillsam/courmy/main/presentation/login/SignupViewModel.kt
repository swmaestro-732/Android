package com.chillsam.courmy.main.presentation.login

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.domain.auth.SignupUseCase
import com.chillsam.courmy.main.entity.auth.SignupProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SignupIntent : MviIntent {
    data class Submit(
        val profile: SignupProfile,
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
                is SignupIntent.Submit -> submit(intent.profile)
                SignupIntent.ConsumeDone -> dispatch(SignupReducerEvent.DoneConsumed)
                SignupIntent.ConsumeError -> dispatch(SignupReducerEvent.ErrorConsumed)
            }
        }

        override fun reduce(
            state: SignupUiState,
            event: SignupReducerEvent,
        ): SignupUiState =
            when (event) {
                SignupReducerEvent.Started -> state.copy(isLoading = true, errorMessage = null)
                SignupReducerEvent.Succeeded -> state.copy(isLoading = false, done = true)
                is SignupReducerEvent.Failed -> state.copy(isLoading = false, errorMessage = event.message)
                SignupReducerEvent.DoneConsumed -> state.copy(done = false)
                SignupReducerEvent.ErrorConsumed -> state.copy(errorMessage = null)
            }

        private fun submit(profile: SignupProfile) {
            if (currentState.isLoading) return
            dispatch(SignupReducerEvent.Started)
            job?.cancel()
            job =
                viewModelScope.launch {
                    runCatching { signupUseCase(profile) }
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

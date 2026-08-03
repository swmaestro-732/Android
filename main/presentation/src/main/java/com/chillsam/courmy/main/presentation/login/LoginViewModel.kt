package com.chillsam.courmy.main.presentation.login

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.main.domain.auth.SocialLoginUseCase
import com.chillsam.courmy.main.entity.auth.SocialProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 로그인 화면 ViewModel. 카카오 SDK 로 받은 idToken 을 서버 social-login 에 넘겨
 * 기존/신규 회원을 분기한다(토큰 저장은 data 레이어가 담당).
 */
@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val socialLoginUseCase: SocialLoginUseCase,
    ) : MviViewModel<LoginIntent, LoginUiState, LoginReducerEvent>(LoginUiState.empty) {
        private var loginJob: Job? = null

        override fun onIntent(intent: LoginIntent) {
            when (intent) {
                is LoginIntent.SocialLogin -> socialLogin(intent.idToken)
                LoginIntent.ConsumeResult -> dispatch(LoginReducerEvent.ResultConsumed)
                LoginIntent.ConsumeError -> dispatch(LoginReducerEvent.ErrorConsumed)
            }
        }

        override fun reduce(
            state: LoginUiState,
            event: LoginReducerEvent,
        ): LoginUiState =
            when (event) {
                LoginReducerEvent.Started -> {
                    state.copy(isLoading = true, errorMessage = null, result = null)
                }

                is LoginReducerEvent.Succeeded -> {
                    state.copy(
                        isLoading = false,
                        result = if (event.newUser) LoginResult.NEW_USER else LoginResult.EXISTING_USER,
                    )
                }

                is LoginReducerEvent.Failed -> {
                    state.copy(isLoading = false, errorMessage = event.message)
                }

                LoginReducerEvent.ResultConsumed -> {
                    state.copy(result = null)
                }

                LoginReducerEvent.ErrorConsumed -> {
                    state.copy(errorMessage = null)
                }
            }

        private fun socialLogin(idToken: String) {
            dispatch(LoginReducerEvent.Started)
            loginJob?.cancel()
            loginJob =
                viewModelScope.launch {
                    runCatching { socialLoginUseCase(SocialProvider.KAKAO, idToken) }
                        .onSuccess { dispatch(LoginReducerEvent.Succeeded(it.newUser)) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            // 원시 예외 메시지(개발자용 requireNotNull·URL 포함 등)는 로그로만 남기고,
                            // 사용자에게는 고정 안내 문구를 보여준다.
                            Log.w(TAG, "소셜 로그인 실패: ${e.javaClass.simpleName} - ${e.message}", e)
                            dispatch(LoginReducerEvent.Failed("로그인에 실패했어요. 잠시 후 다시 시도해 주세요."))
                        }
                }
        }

        private companion object {
            const val TAG = "Login"
        }
    }

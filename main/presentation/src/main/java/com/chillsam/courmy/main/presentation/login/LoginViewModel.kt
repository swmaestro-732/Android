package com.chillsam.courmy.main.presentation.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.domain.telemetry.AppFailure
import com.chillsam.courmy.common.domain.telemetry.AppFlow
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.main.domain.auth.SocialLoginUseCase
import com.chillsam.courmy.main.entity.auth.SocialProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 로그인 화면 ViewModel. 카카오 SDK 로그인(idToken 획득)부터 서버 social-login 까지를 담당한다.
 * 기존 회원이면 홈, 신규 회원이면 가입 플로우로 분기한다(토큰 저장은 data 레이어).
 */
@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val socialLoginUseCase: SocialLoginUseCase,
        private val telemetry: Telemetry,
    ) : MviViewModel<LoginIntent, LoginUiState, LoginReducerEvent>(LoginUiState.empty) {
        private var loginJob: Job? = null

        override fun onIntent(intent: LoginIntent) {
            when (intent) {
                is LoginIntent.KakaoLogin -> kakaoLogin(intent.context)
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

                LoginReducerEvent.Canceled -> {
                    state.copy(isLoading = false)
                }
            }

        /**
         * 카카오 SDK 로그인 → 서버 social-login 을 **하나의 job** 으로 묶는다.
         *
         * SDK 단계를 [viewModelScope] 에서 돌리는 게 핵심이다. 이전에는 화면의
         * `rememberCoroutineScope` 를 썼는데, 카카오톡으로 나간 사이 화면이 사라지면 스코프가 취소돼
         * 돌아온 결과가 조용히 버려졌다 — 토스트도 로딩도 없이 로그인 화면에 그대로 머무른다.
         *
         * 두 단계를 한 job 으로 묶은 이유: 나눠 두면 뒤 단계가 앞 단계의 job 을 cancel 해 버리는
         * 사고가 나기 쉽다(같은 [loginJob] 을 공유하므로 자기 자신을 취소한다).
         */
        private fun kakaoLogin(context: Context) {
            // 진행 중이면 무시한다. 버튼도 isLoading 으로 잠그지만, 화면 재구성 등으로 상태가
            // 초기화되면 중복 진입이 가능하다. 그때 continuation 이 여러 개 생기면 결과가 뒤섞인다.
            if (loginJob?.isActive == true) return
            dispatch(LoginReducerEvent.Started)
            loginJob =
                viewModelScope.launch {
                    val idToken = obtainIdToken(context) ?: return@launch
                    requestSocialLogin(idToken)
                }
        }

        /** 카카오 SDK 단계. 실패·취소는 여기서 상태로 반영하고 null 을 돌려 흐름을 끊는다. */
        private suspend fun obtainIdToken(context: Context): String? =
            runCatching { KakaoLoginClient.login(context) }
                .getOrElse { e ->
                    when {
                        e is CancellationException -> throw e

                        // 사용자가 스스로 취소한 것은 실패가 아니다. 안내도 리포트도 남기지 않는다.
                        e is KakaoLoginClient.CanceledException -> dispatch(LoginReducerEvent.Canceled)

                        else -> reportKakaoFailure(context, e)
                    }
                    null
                }

        /**
         * 카카오 SDK 로그인 실패를 **원격에도** 남긴다.
         *
         * 이전에는 `Log.w` + 토스트뿐이라, 기기별로 갈리는 실패(예: Play 다중 서명으로 인한
         * keyHash 불일치)를 확인하려면 테스터 폰마다 USB 를 꽂아 logcat 을 봐야 했다.
         * [KakaoLoginClient.diagnostics] 로 그 기기가 실제로 보낸 키 해시를 함께 실어 보낸다.
         */
        private fun reportKakaoFailure(
            context: Context,
            e: Throwable,
        ) {
            Log.w(TAG, "카카오 SDK 로그인 실패: ${e.javaClass.simpleName} - ${e.message}", e)
            telemetry.recordFailure(
                AppFailure(
                    area = AppFlow.Login.eventName,
                    kind = "kakao_sdk_${e.javaClass.simpleName}",
                    detail = KakaoLoginClient.diagnostics(context),
                    cause = e,
                ),
            )
            dispatch(LoginReducerEvent.Failed("카카오 로그인을 완료하지 못했어요."))
        }

        /** 서버 social-login 단계. 성공/실패 계측은 [SocialLoginUseCase] 의 track 이 담당한다. */
        private suspend fun requestSocialLogin(idToken: String) {
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

        private companion object {
            const val TAG = "Login"
        }
    }

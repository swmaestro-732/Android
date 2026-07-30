package com.chillsam.courmy.main.presentation.settings

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.domain.auth.LogoutUseCase
import com.chillsam.courmy.main.domain.auth.WithdrawUseCase
import com.chillsam.courmy.main.domain.my.GetMyProfileUseCase
import com.chillsam.courmy.main.presentation.login.KakaoLoginClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AccountIntent : MviIntent {
    data object Logout : AccountIntent

    data object Withdraw : AccountIntent

    data object ConsumeResult : AccountIntent

    data object ConsumeError : AccountIntent
}

/** 로그아웃/탈퇴 완료 후 게스트 화면으로 보낼 1회성 신호. */
enum class AccountAction {
    LOGGED_OUT,
    WITHDRAWN,
}

data class AccountUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val result: AccountAction? = null,
) : UiState {
    companion object {
        val empty = AccountUiState()
    }
}

sealed interface AccountReducerEvent : ReducerEvent {
    data object Started : AccountReducerEvent

    data class Finished(
        val action: AccountAction,
    ) : AccountReducerEvent

    data class Failed(
        val message: String,
    ) : AccountReducerEvent

    data object ResultConsumed : AccountReducerEvent

    data object ErrorConsumed : AccountReducerEvent
}

/**
 * 계정 관리(로그아웃·회원 탈퇴) ViewModel.
 * 탈퇴는 현재 사용자 id 가 필요해 프로필을 조회해 얻은 뒤 삭제한다. 카카오 세션도 함께 정리한다.
 */
@HiltViewModel
class AccountViewModel
    @Inject
    constructor(
        private val logoutUseCase: LogoutUseCase,
        private val withdrawUseCase: WithdrawUseCase,
        private val getMyProfileUseCase: GetMyProfileUseCase,
    ) : MviViewModel<AccountIntent, AccountUiState, AccountReducerEvent>(AccountUiState.empty) {
        private var job: Job? = null

        override fun onIntent(intent: AccountIntent) {
            when (intent) {
                AccountIntent.Logout -> logout()
                AccountIntent.Withdraw -> withdraw()
                AccountIntent.ConsumeResult -> dispatch(AccountReducerEvent.ResultConsumed)
                AccountIntent.ConsumeError -> dispatch(AccountReducerEvent.ErrorConsumed)
            }
        }

        override fun reduce(
            state: AccountUiState,
            event: AccountReducerEvent,
        ): AccountUiState =
            when (event) {
                AccountReducerEvent.Started -> state.copy(isLoading = true, errorMessage = null)
                is AccountReducerEvent.Finished -> state.copy(isLoading = false, result = event.action)
                is AccountReducerEvent.Failed -> state.copy(isLoading = false, errorMessage = event.message)
                AccountReducerEvent.ResultConsumed -> state.copy(result = null)
                AccountReducerEvent.ErrorConsumed -> state.copy(errorMessage = null)
            }

        private fun logout() {
            if (currentState.isLoading) return
            dispatch(AccountReducerEvent.Started)
            job?.cancel()
            job =
                viewModelScope.launch {
                    runCatching {
                        logoutUseCase()
                        runCatching { KakaoLoginClient.logout() }
                    }.onSuccess { dispatch(AccountReducerEvent.Finished(AccountAction.LOGGED_OUT)) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "로그아웃 실패: ${e.message}", e)
                            // 서버 실패해도 로컬 세션은 정리되므로 로그아웃 완료로 처리.
                            dispatch(AccountReducerEvent.Finished(AccountAction.LOGGED_OUT))
                        }
                }
        }

        private fun withdraw() {
            if (currentState.isLoading) return
            dispatch(AccountReducerEvent.Started)
            job?.cancel()
            job =
                viewModelScope.launch {
                    runCatching {
                        val userId = getMyProfileUseCase().id
                        withdrawUseCase(userId)
                        runCatching { KakaoLoginClient.unlink() }
                    }.onSuccess { dispatch(AccountReducerEvent.Finished(AccountAction.WITHDRAWN)) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "회원 탈퇴 실패: ${e.message}", e)
                            dispatch(AccountReducerEvent.Failed(e.message ?: "회원 탈퇴에 실패했어요."))
                        }
                }
        }

        private companion object {
            const val TAG = "Account"
        }
    }

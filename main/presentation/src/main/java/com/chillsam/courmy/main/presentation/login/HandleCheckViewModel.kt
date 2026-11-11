package com.chillsam.courmy.main.presentation.login

import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.domain.auth.CheckHandleUseCase
import com.chillsam.courmy.main.entity.auth.HandleCheckResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HandleCheckIntent : MviIntent {
    /** "중복 확인" 탭. */
    data class Check(
        val handle: String,
    ) : HandleCheckIntent

    /** 입력이 바뀌어 이전 판정을 무효화. */
    data object Reset : HandleCheckIntent
}

/**
 * [result] 가 null 이면 아직 확인하지 않은 상태다.
 * [checkedHandle] 은 판정 당시의 입력으로, 화면이 "지금 입력값과 같은지" 확인하는 데 쓴다.
 */
data class HandleCheckUiState(
    val isChecking: Boolean = false,
    val result: HandleCheckResult? = null,
    val checkedHandle: String = "",
) : UiState {
    companion object {
        val empty = HandleCheckUiState()
    }
}

sealed interface HandleCheckReducerEvent : ReducerEvent {
    data object Started : HandleCheckReducerEvent

    data class Checked(
        val result: HandleCheckResult,
        val handle: String,
    ) : HandleCheckReducerEvent

    data object Reset : HandleCheckReducerEvent
}

/**
 * 아이디 중복 확인 ViewModel. 프로필 생성(FS-05)과 프로필 수정(FS-26)이 같은 규칙을 쓰므로 공유한다.
 * 실제 판정은 [CheckHandleUseCase] 가 하고(길이·형식 → 서버 조회), 여기서는 진행 상태만 관리한다.
 */
@HiltViewModel
class HandleCheckViewModel
    @Inject
    constructor(
        private val checkHandleUseCase: CheckHandleUseCase,
    ) : MviViewModel<HandleCheckIntent, HandleCheckUiState, HandleCheckReducerEvent>(
            HandleCheckUiState.empty,
        ) {
        private var job: Job? = null

        override fun onIntent(intent: HandleCheckIntent) {
            when (intent) {
                is HandleCheckIntent.Check -> {
                    check(intent.handle)
                }

                HandleCheckIntent.Reset -> {
                    job?.cancel()
                    dispatch(HandleCheckReducerEvent.Reset)
                }
            }
        }

        override fun reduce(
            state: HandleCheckUiState,
            event: HandleCheckReducerEvent,
        ): HandleCheckUiState =
            when (event) {
                HandleCheckReducerEvent.Started -> {
                    state.copy(isChecking = true, result = null)
                }

                is HandleCheckReducerEvent.Checked -> {
                    state.copy(isChecking = false, result = event.result, checkedHandle = event.handle)
                }

                HandleCheckReducerEvent.Reset -> {
                    state.copy(isChecking = false, result = null, checkedHandle = "")
                }
            }

        private fun check(handle: String) {
            if (currentState.isChecking) return
            dispatch(HandleCheckReducerEvent.Started)
            job?.cancel()
            job =
                viewModelScope.launch {
                    // UseCase 가 네트워크 실패까지 ERROR 로 바꿔 주므로 여기서 예외를 다루지 않는다.
                    val result = checkHandleUseCase(handle)
                    dispatch(HandleCheckReducerEvent.Checked(result, handle))
                }
        }
    }

/** 확인 전 보여 주는 아이디 입력 조건. [message] 와 마찬가지로 회원가입·프로필 편집이 같은 문구를 쓴다. */
@androidx.compose.runtime.Composable
fun handleRules() =
    listOf(
        androidx.compose.ui.res.stringResource(
            com.chillsam.courmy.common.presentation.R.string.message_handle_allowed_characters_hint,
        ),
        androidx.compose.ui.res.stringResource(
            com.chillsam.courmy.common.presentation.R.string.message_handle_length_hint,
        ),
    )

/** 판정 결과별 안내 문구. 화면 두 곳이 같은 문구를 쓴다. */
@androidx.compose.runtime.Composable
fun HandleCheckResult.message(): String =
    when (this) {
        HandleCheckResult.AVAILABLE -> {
            androidx.compose.ui.res.stringResource(
                com.chillsam.courmy.common.presentation.R.string.message_handle_available,
            )
        }

        HandleCheckResult.INVALID_LENGTH -> {
            androidx.compose.ui.res.stringResource(
                com.chillsam.courmy.common.presentation.R.string.message_handle_invalid_length,
            )
        }

        HandleCheckResult.INVALID_FORMAT -> {
            androidx.compose.ui.res.stringResource(
                com.chillsam.courmy.common.presentation.R.string.message_handle_invalid_format,
            )
        }

        HandleCheckResult.TAKEN -> {
            androidx.compose.ui.res.stringResource(
                com.chillsam.courmy.common.presentation.R.string.message_handle_taken,
            )
        }

        HandleCheckResult.ERROR -> {
            androidx.compose.ui.res.stringResource(
                com.chillsam.courmy.common.presentation.R.string.message_handle_check_failed,
            )
        }
    }

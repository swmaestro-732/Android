package com.chillsam.courmy.main.presentation.update

import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.domain.appUpdate.AppUpdateEventBus
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.main.domain.update.CheckAppVersionUseCase
import com.chillsam.courmy.main.entity.update.AppVersionCheckVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

/**
 * 서버 강제 업데이트는 그대로 전달하고 Remote Config 선택 업데이트는 Activity 수명 동안 한 번 확인한다.
 * ViewModel이 재사용되는 화면 회전·스토어 복귀에서 조회와 안내를 반복하지 않는다.
 */
@HiltViewModel
class AppUpdateViewModel
    @Inject
    constructor(
        appUpdateEventBus: AppUpdateEventBus,
        private val checkAppVersion: CheckAppVersionUseCase,
        private val reminder: OptionalUpdateReminder,
    ) : MviViewModel<AppUpdateIntent, AppUpdateUIState, AppUpdateReducerEvent>(AppUpdateUIState()) {
        val updateRequired: StateFlow<Boolean> = appUpdateEventBus.updateRequired

        override fun onIntent(intent: AppUpdateIntent) {
            when (intent) {
                AppUpdateIntent.CheckOptionalUpdate -> {
                    checkOptionalUpdate()
                }

                AppUpdateIntent.DismissOptionalUpdate -> {
                    reminder.dismissForToday()
                    dispatch(AppUpdateReducerEvent.Dismissed)
                }

                AppUpdateIntent.AcceptOptionalUpdate -> {
                    dispatch(AppUpdateReducerEvent.Dismissed)
                }
            }
        }

        private fun checkOptionalUpdate() {
            if (currentState.checked || updateRequired.value) return
            dispatch(AppUpdateReducerEvent.CheckStarted)
            if (reminder.isDismissedToday()) return
            viewModelScope.launch {
                val available =
                    try {
                        withTimeoutOrNull(UPDATE_CHECK_TIMEOUT_MS) {
                            (checkAppVersion() as? AppVersionCheckVO.Checked)?.updateAvailable == true
                        } ?: false
                    } catch (error: CancellationException) {
                        throw error
                    } catch (_: Exception) {
                        // 설정 누락·오프라인 등으로 조회할 수 없어도 앱 진입을 막지 않는다.
                        false
                    }
                dispatch(AppUpdateReducerEvent.Checked(available && !updateRequired.value))
            }
        }

        override fun reduce(
            state: AppUpdateUIState,
            event: AppUpdateReducerEvent,
        ): AppUpdateUIState =
            when (event) {
                AppUpdateReducerEvent.CheckStarted -> state.copy(checked = true)
                is AppUpdateReducerEvent.Checked -> state.copy(showOptionalUpdate = event.available)
                AppUpdateReducerEvent.Dismissed -> state.copy(showOptionalUpdate = false)
            }

        private companion object {
            const val UPDATE_CHECK_TIMEOUT_MS = 5_000L
        }
    }

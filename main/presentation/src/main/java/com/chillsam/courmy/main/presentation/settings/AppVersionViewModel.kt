package com.chillsam.courmy.main.presentation.settings

import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.main.domain.update.CheckAppVersionUseCase
import com.chillsam.courmy.main.entity.update.AppVersionCheckVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@HiltViewModel
class AppVersionViewModel
    @Inject
    constructor(
        private val checkAppVersion: CheckAppVersionUseCase,
    ) : MviViewModel<AppVersionIntent, AppVersionUIState, AppVersionReducerEvent>(AppVersionUIState()) {
        private var checkJob: Job? = null

        override fun onIntent(intent: AppVersionIntent) {
            when (intent) {
                AppVersionIntent.Check -> check()
            }
        }

        private fun check() {
            if (checkJob?.isActive == true) return
            dispatch(AppVersionReducerEvent.CheckStarted)
            checkJob =
                viewModelScope.launch {
                    val result =
                        try {
                            withTimeoutOrNull(CHECK_TIMEOUT_MS) { checkAppVersion(forceRefresh = true) }
                                ?: AppVersionCheckVO.Unavailable
                        } catch (error: CancellationException) {
                            throw error
                        } catch (_: Exception) {
                            AppVersionCheckVO.Unavailable
                        }
                    val status =
                        when (result) {
                            is AppVersionCheckVO.Checked -> {
                                if (result.updateAvailable) {
                                    AppVersionStatus.UpdateAvailable
                                } else {
                                    AppVersionStatus.Latest
                                }
                            }

                            AppVersionCheckVO.Unavailable -> {
                                AppVersionStatus.Unavailable
                            }
                        }
                    val latestVersion =
                        (result as? AppVersionCheckVO.Checked)?.let {
                            it.latestVersionName.ifBlank { "빌드 ${it.latestVersionCode}" }
                        }
                    dispatch(AppVersionReducerEvent.Checked(status, latestVersion))
                }
        }

        override fun reduce(
            state: AppVersionUIState,
            event: AppVersionReducerEvent,
        ): AppVersionUIState =
            when (event) {
                AppVersionReducerEvent.CheckStarted -> {
                    state.copy(
                        status = AppVersionStatus.Loading,
                        latestVersion = null,
                    )
                }

                is AppVersionReducerEvent.Checked -> {
                    state.copy(
                        status = event.status,
                        latestVersion = event.latestVersion,
                    )
                }
            }

        private companion object {
            const val CHECK_TIMEOUT_MS = 5_000L
        }
    }

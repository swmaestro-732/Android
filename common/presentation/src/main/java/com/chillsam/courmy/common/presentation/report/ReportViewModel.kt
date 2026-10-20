package com.chillsam.courmy.common.presentation.report

import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.domain.report.ReportSubmissionException
import com.chillsam.courmy.common.domain.report.SubmitReportUseCase
import com.chillsam.courmy.common.entity.report.ReportReason
import com.chillsam.courmy.common.entity.report.ReportTargetType
import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.common.presentation.mvi.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportUIState(
    val reason: ReportReason? = null,
    val description: String = "",
    val submitting: Boolean = false,
    val error: String? = null,
    val success: String? = null,
) : UiState

sealed interface ReportIntent : MviIntent {
    data class SelectReason(
        val reason: ReportReason,
    ) : ReportIntent

    data class Describe(
        val value: String,
    ) : ReportIntent

    data class Submit(
        val type: ReportTargetType,
        val id: Long,
    ) : ReportIntent

    data object Reset : ReportIntent
}

sealed interface ReportEvent : ReducerEvent {
    data class ReasonChanged(
        val reason: ReportReason,
    ) : ReportEvent

    data class DescriptionChanged(
        val value: String,
    ) : ReportEvent

    data object Started : ReportEvent

    data class Failed(
        val message: String,
    ) : ReportEvent

    data class Succeeded(
        val message: String,
    ) : ReportEvent

    data object Reset : ReportEvent
}

@HiltViewModel
class ReportViewModel
    @Inject
    constructor(
        private val submitReport: SubmitReportUseCase,
    ) : MviViewModel<ReportIntent, ReportUIState, ReportEvent>(ReportUIState()) {
        override fun onIntent(intent: ReportIntent) {
            if (currentState.submitting) return
            when (intent) {
                is ReportIntent.SelectReason -> {
                    dispatch(ReportEvent.ReasonChanged(intent.reason))
                }

                is ReportIntent.Describe -> {
                    dispatch(
                        ReportEvent.DescriptionChanged(intent.value.take(SubmitReportUseCase.MAX_DESCRIPTION_LENGTH)),
                    )
                }

                is ReportIntent.Submit -> {
                    submit(intent)
                }

                ReportIntent.Reset -> {
                    dispatch(ReportEvent.Reset)
                }
            }
        }

        private fun submit(intent: ReportIntent.Submit) {
            val state = currentState
            val reason = state.reason ?: return
            if (state.success != null) return
            dispatch(ReportEvent.Started)
            viewModelScope.launch {
                runCatching { submitReport(intent.type, intent.id, reason, state.description) }
                    .onSuccess { result -> dispatch(ReportEvent.Succeeded(result.message)) }
                    .onFailure { error ->
                        if (error is CancellationException) throw error
                        dispatch(
                            ReportEvent.Failed(
                                (error as? ReportSubmissionException)?.message ?: "신고하지 못했어요. 다시 시도해 주세요.",
                            ),
                        )
                    }
            }
        }

        override fun reduce(
            state: ReportUIState,
            event: ReportEvent,
        ): ReportUIState =
            when (event) {
                is ReportEvent.ReasonChanged -> state.copy(reason = event.reason)
                is ReportEvent.DescriptionChanged -> state.copy(description = event.value)
                ReportEvent.Started -> state.copy(submitting = true, error = null)
                is ReportEvent.Failed -> state.copy(submitting = false, error = event.message)
                is ReportEvent.Succeeded -> state.copy(submitting = false, success = event.message)
                ReportEvent.Reset -> ReportUIState()
            }
    }

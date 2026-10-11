package com.chillsam.courmy.course.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.domain.error.HttpResponseException
import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.course.domain.GetPlanDetailUseCase
import com.chillsam.courmy.course.entity.PlanDetailVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

data class PlanDetailUIState(
    val isLoading: Boolean = false,
    val detail: PlanDetailVO? = null,
    val errorMessage: String? = null,
) : UiState

sealed interface PlanDetailIntent : MviIntent {
    data class Load(
        val planId: Long,
    ) : PlanDetailIntent

    data object Retry : PlanDetailIntent
}

sealed interface PlanDetailReducerEvent : ReducerEvent {
    data object Loading : PlanDetailReducerEvent

    data class Loaded(
        val detail: PlanDetailVO,
    ) : PlanDetailReducerEvent

    data class Failed(
        val message: String,
    ) : PlanDetailReducerEvent
}

@HiltViewModel
class PlanDetailViewModel
    @Inject
    constructor(
        private val getPlanDetail: GetPlanDetailUseCase,
    ) : MviViewModel<PlanDetailIntent, PlanDetailUIState, PlanDetailReducerEvent>(PlanDetailUIState()) {
        private var planId = 0L
        private var loadJob: Job? = null

        override fun onIntent(intent: PlanDetailIntent) {
            when (intent) {
                is PlanDetailIntent.Load -> {
                    if (planId == intent.planId && (currentState.detail != null || currentState.isLoading)) return
                    planId = intent.planId
                    load()
                }

                PlanDetailIntent.Retry -> {
                    load()
                }
            }
        }

        private fun load() {
            loadJob?.cancel()
            loadJob =
                viewModelScope.launch {
                    dispatch(PlanDetailReducerEvent.Loading)
                    runCatching { getPlanDetail(planId) }
                        .onSuccess { dispatch(PlanDetailReducerEvent.Loaded(it)) }
                        .onFailure { error ->
                            if (error is CancellationException) throw error
                            Log.w("PlanDetail", "계획 상세 조회 실패", error)
                            dispatch(PlanDetailReducerEvent.Failed(error.planErrorMessage()))
                        }
                }
        }

        override fun reduce(
            state: PlanDetailUIState,
            event: PlanDetailReducerEvent,
        ): PlanDetailUIState =
            when (event) {
                PlanDetailReducerEvent.Loading -> PlanDetailUIState(isLoading = true)
                is PlanDetailReducerEvent.Loaded -> PlanDetailUIState(detail = event.detail)
                is PlanDetailReducerEvent.Failed -> PlanDetailUIState(errorMessage = event.message)
            }
    }

private fun Throwable.planErrorMessage(): String {
    val serverMessage =
        if (this is HttpResponseException) {
            runCatching {
                Json
                    .parseToJsonElement(cause?.message.orEmpty())
                    .jsonObject["message"]
                    ?.jsonPrimitive
                    ?.contentOrNull
            }.getOrNull()
        } else {
            null
        }
    return serverMessage?.takeIf { it.isNotBlank() } ?: "계획을 불러오지 못했어요. 다시 시도해 주세요."
}

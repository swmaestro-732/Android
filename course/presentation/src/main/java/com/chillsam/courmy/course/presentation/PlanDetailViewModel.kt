package com.chillsam.courmy.course.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.domain.error.HttpResponseException
import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.course.domain.DeletePlanUseCase
import com.chillsam.courmy.course.domain.EditPlanUseCase
import com.chillsam.courmy.course.domain.GetPlanDetailUseCase
import com.chillsam.courmy.course.entity.PlanDetailVO
import com.chillsam.courmy.course.entity.PlanInputVO
import com.chillsam.courmy.course.entity.PlanPlaceInputVO
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
    /** 수정·삭제 요청 진행 중(중복 탭 방지 + 버튼 문구). */
    val isMutating: Boolean = false,
    /** 삭제 완료 신호. 화면이 소비하고 목록으로 되돌아간다. */
    val deleted: Boolean = false,
    /** 수정·삭제 실패 등 일회성 안내. 토스트로 보여주고 [PlanDetailIntent.ConsumeMessage] 로 비운다. */
    val message: String? = null,
) : UiState

sealed interface PlanDetailIntent : MviIntent {
    data class Load(
        val planId: Long,
    ) : PlanDetailIntent

    data object Retry : PlanDetailIntent

    /** 수정 다이얼로그 저장. 장소 구성은 바꾸지 않으므로 상세의 기존 장소를 그대로 보낸다. */
    data class Edit(
        val title: String,
        val memo: String,
        val plannedDate: String,
    ) : PlanDetailIntent

    data object Delete : PlanDetailIntent

    data object ConsumeMessage : PlanDetailIntent
}

sealed interface PlanDetailReducerEvent : ReducerEvent {
    data object Loading : PlanDetailReducerEvent

    data class Loaded(
        val detail: PlanDetailVO,
    ) : PlanDetailReducerEvent

    data class Failed(
        val message: String,
    ) : PlanDetailReducerEvent

    data object MutationStarted : PlanDetailReducerEvent

    /** 수정 반영 완료. 갱신된 상세로 교체한다. */
    data class Edited(
        val detail: PlanDetailVO,
    ) : PlanDetailReducerEvent

    data object Deleted : PlanDetailReducerEvent

    data class MutationFailed(
        val message: String,
    ) : PlanDetailReducerEvent

    data object MessageConsumed : PlanDetailReducerEvent
}

@HiltViewModel
class PlanDetailViewModel
    @Inject
    constructor(
        private val getPlanDetail: GetPlanDetailUseCase,
        private val editPlan: EditPlanUseCase,
        private val deletePlan: DeletePlanUseCase,
    ) : MviViewModel<PlanDetailIntent, PlanDetailUIState, PlanDetailReducerEvent>(PlanDetailUIState()) {
        private var planId = 0L
        private var loadJob: Job? = null
        private var mutateJob: Job? = null

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

                is PlanDetailIntent.Edit -> {
                    edit(intent)
                }

                PlanDetailIntent.Delete -> {
                    delete()
                }

                PlanDetailIntent.ConsumeMessage -> {
                    dispatch(PlanDetailReducerEvent.MessageConsumed)
                }
            }
        }

        /** 제목·메모·날짜만 바꾼다. places 는 전체 교체 계약이라 상세의 기존 구성을 그대로 실어 보낸다. */
        private fun edit(intent: PlanDetailIntent.Edit) {
            val detail = currentState.detail ?: return
            if (mutateJob?.isActive == true) return
            dispatch(PlanDetailReducerEvent.MutationStarted)
            mutateJob =
                viewModelScope.launch {
                    runCatching {
                        editPlan(
                            planId = planId,
                            plan =
                                PlanInputVO(
                                    title = intent.title,
                                    memo = intent.memo.takeIf { it.isNotBlank() },
                                    plannedDate = intent.plannedDate.takeIf { it.isNotBlank() },
                                    places =
                                        detail.places.map { place ->
                                            PlanPlaceInputVO(
                                                placeId = place.placeId,
                                                memo = place.memo.takeIf { it.isNotBlank() },
                                                walkingMinutes = place.walkingMinutesToNext,
                                            )
                                        },
                                ),
                        )
                        // 서버 정규화(trim 등)를 반영하려고 수정 후 상세를 다시 받는다.
                        getPlanDetail(planId)
                    }.onSuccess { dispatch(PlanDetailReducerEvent.Edited(it)) }
                        .onFailure { error ->
                            if (error is CancellationException) throw error
                            Log.w("PlanDetail", "계획 수정 실패", error)
                            dispatch(
                                PlanDetailReducerEvent.MutationFailed(
                                    (error as? IllegalArgumentException)?.message
                                        ?: error.planErrorMessage("계획을 수정하지 못했어요."),
                                ),
                            )
                        }
                }
        }

        private fun delete() {
            if (mutateJob?.isActive == true) return
            dispatch(PlanDetailReducerEvent.MutationStarted)
            mutateJob =
                viewModelScope.launch {
                    runCatching { deletePlan(planId) }
                        .onSuccess { dispatch(PlanDetailReducerEvent.Deleted) }
                        .onFailure { error ->
                            if (error is CancellationException) throw error
                            Log.w("PlanDetail", "계획 삭제 실패", error)
                            dispatch(
                                PlanDetailReducerEvent.MutationFailed(error.planErrorMessage("계획을 삭제하지 못했어요.")),
                            )
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
                PlanDetailReducerEvent.MutationStarted -> state.copy(isMutating = true, message = null)
                is PlanDetailReducerEvent.Edited -> state.copy(isMutating = false, detail = event.detail)
                PlanDetailReducerEvent.Deleted -> state.copy(isMutating = false, deleted = true)
                is PlanDetailReducerEvent.MutationFailed -> state.copy(isMutating = false, message = event.message)
                PlanDetailReducerEvent.MessageConsumed -> state.copy(message = null)
            }
    }

private fun Throwable.planErrorMessage(fallback: String = "계획을 불러오지 못했어요. 다시 시도해 주세요."): String {
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
    return serverMessage?.takeIf { it.isNotBlank() } ?: fallback
}

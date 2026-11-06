package com.chillsam.courmy.main.presentation.my

import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.domain.my.GetMyProfileUseCase
import com.chillsam.courmy.main.domain.profile.SaveInterestsUseCase
import com.chillsam.courmy.main.entity.area.AreaVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface InterestEditIntent : MviIntent {
    /** 편집 시작 시 지금 저장된 관심사를 불러온다. */
    data object Load : InterestEditIntent

    data class SaveThemes(
        val themes: List<String>,
    ) : InterestEditIntent

    data class SaveRegions(
        val regions: List<AreaVO>,
    ) : InterestEditIntent
}

data class InterestEditUiState(
    /** 저장된 값을 아직 못 읽었으면 false. 화면은 이때까지 선택 상태를 확정하지 않는다. */
    val loaded: Boolean = false,
    val isLoading: Boolean = false,
    val loadError: Boolean = false,
    val isSaving: Boolean = false,
    val saveError: Boolean = false,
    val themes: List<String> = emptyList(),
    val regions: List<AreaVO> = emptyList(),
    /** 저장이 끝나 화면을 닫아도 되는 1회성 신호. */
    val saved: Boolean = false,
) : UiState {
    companion object {
        val empty = InterestEditUiState()
    }
}

sealed interface InterestEditReducerEvent : ReducerEvent {
    data object Loading : InterestEditReducerEvent

    data object LoadFailed : InterestEditReducerEvent

    data object Saving : InterestEditReducerEvent

    data object SaveFailed : InterestEditReducerEvent

    data class Loaded(
        val themes: List<String>,
        val regions: List<AreaVO>,
    ) : InterestEditReducerEvent

    data object Saved : InterestEditReducerEvent
}

/** 서버에서 읽은 관심사를 편집하며 조회·저장 실패 시 기존 선택을 보호한다. */
@HiltViewModel
class InterestEditViewModel
    @Inject
    constructor(
        private val getMyProfileUseCase: GetMyProfileUseCase,
        private val saveInterestsUseCase: SaveInterestsUseCase,
    ) : MviViewModel<InterestEditIntent, InterestEditUiState, InterestEditReducerEvent>(InterestEditUiState.empty) {
        override fun onIntent(intent: InterestEditIntent) {
            when (intent) {
                InterestEditIntent.Load -> load()
                is InterestEditIntent.SaveThemes -> save { saveInterestsUseCase.saveThemes(intent.themes) }
                is InterestEditIntent.SaveRegions -> save { saveInterestsUseCase.saveRegions(intent.regions) }
            }
        }

        override fun reduce(
            state: InterestEditUiState,
            event: InterestEditReducerEvent,
        ): InterestEditUiState =
            when (event) {
                InterestEditReducerEvent.Loading -> {
                    state.copy(isLoading = true, loadError = false)
                }

                InterestEditReducerEvent.LoadFailed -> {
                    state.copy(isLoading = false, loadError = true)
                }

                InterestEditReducerEvent.Saving -> {
                    state.copy(isSaving = true, saveError = false)
                }

                InterestEditReducerEvent.SaveFailed -> {
                    state.copy(isSaving = false, saveError = true)
                }

                is InterestEditReducerEvent.Loaded -> {
                    state.copy(
                        loaded = true,
                        isLoading = false,
                        loadError = false,
                        themes = event.themes,
                        regions = event.regions,
                    )
                }

                InterestEditReducerEvent.Saved -> {
                    state.copy(saved = true, isSaving = false)
                }
            }

        private fun load() {
            if (currentState.loaded || currentState.isLoading) return
            dispatch(InterestEditReducerEvent.Loading)
            viewModelScope.launch {
                try {
                    val profile = getMyProfileUseCase()
                    dispatch(InterestEditReducerEvent.Loaded(profile.interestThemes, profile.interestRegions))
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    dispatch(InterestEditReducerEvent.LoadFailed)
                }
            }
        }

        private fun save(block: suspend () -> Unit) {
            if (!currentState.loaded || currentState.isSaving || currentState.saved) return
            dispatch(InterestEditReducerEvent.Saving)
            viewModelScope.launch {
                try {
                    block()
                    dispatch(InterestEditReducerEvent.Saved)
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    dispatch(InterestEditReducerEvent.SaveFailed)
                }
            }
        }
    }

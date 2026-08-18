package com.chillsam.courmy.main.presentation.my

import android.util.Log
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
import kotlinx.coroutines.Job
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
    data class Loaded(
        val themes: List<String>,
        val regions: List<AreaVO>,
    ) : InterestEditReducerEvent

    data object Saved : InterestEditReducerEvent
}

/**
 * 관심 테마·지역 편집 ViewModel.
 *
 * 저장된 관심사는 마이페이지 응답이 아니라 기기에 있으므로([InterestPreferencesDataStore] 주석 참고)
 * 프로필 조회로 함께 읽어 온다 — repository 가 응답에 로컬 값을 합쳐 주기 때문이다.
 */
@HiltViewModel
class InterestEditViewModel
    @Inject
    constructor(
        private val getMyProfileUseCase: GetMyProfileUseCase,
        private val saveInterestsUseCase: SaveInterestsUseCase,
    ) : MviViewModel<InterestEditIntent, InterestEditUiState, InterestEditReducerEvent>(InterestEditUiState.empty) {
        private var job: Job? = null

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
                is InterestEditReducerEvent.Loaded -> {
                    state.copy(loaded = true, themes = event.themes, regions = event.regions)
                }

                InterestEditReducerEvent.Saved -> {
                    state.copy(saved = true)
                }
            }

        private fun load() {
            if (currentState.loaded) return
            job?.cancel()
            job =
                viewModelScope.launch {
                    runCatching { getMyProfileUseCase() }
                        .onSuccess {
                            dispatch(InterestEditReducerEvent.Loaded(it.interestThemes, it.interestRegions))
                        }.onFailure { e ->
                            if (e is CancellationException) throw e
                            // 조회에 실패해도 편집은 계속할 수 있어야 한다(빈 선택으로 시작).
                            Log.w(TAG, "관심사 불러오기 실패: ${e.message}", e)
                            dispatch(InterestEditReducerEvent.Loaded(emptyList(), emptyList()))
                        }
                }
        }

        /** 저장은 기기 쓰기라 사실상 실패하지 않지만, 실패해도 화면은 닫아 준다(값은 다시 고르면 된다). */
        private fun save(block: suspend () -> Unit) {
            job?.cancel()
            job =
                viewModelScope.launch {
                    runCatching { block() }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "관심사 저장 실패: ${e.message}", e)
                        }
                    dispatch(InterestEditReducerEvent.Saved)
                }
        }

        private companion object {
            const val TAG = "InterestEdit"
        }
    }

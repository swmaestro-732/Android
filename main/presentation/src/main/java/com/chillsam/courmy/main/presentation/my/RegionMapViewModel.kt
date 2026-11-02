package com.chillsam.courmy.main.presentation.my

import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.domain.area.GetAreaMapUseCase
import com.chillsam.courmy.main.domain.my.GetMyProfileUseCase
import com.chillsam.courmy.main.domain.profile.SaveInterestsUseCase
import com.chillsam.courmy.main.entity.area.AreaLevel
import com.chillsam.courmy.main.entity.area.AreaMapVO
import com.chillsam.courmy.main.entity.area.AreaVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RegionMapIntent : MviIntent {
    data class Initialize(
        val editing: Boolean,
        val regions: List<AreaVO>,
    ) : RegionMapIntent

    data object Retry : RegionMapIntent

    data class Browse(
        val code: String?,
    ) : RegionMapIntent

    data class Toggle(
        val code: String,
    ) : RegionMapIntent

    data class Remove(
        val code: String,
    ) : RegionMapIntent

    data object Save : RegionMapIntent
}

data class RegionMapUiState(
    val regions: ImmutableList<AreaMapVO> = persistentListOf(),
    val selected: ImmutableList<AreaVO> = persistentListOf(),
    val provinceCode: String? = null,
    val selectionFocusCode: String? = null,
    val isLoading: Boolean = true,
    val loadError: Boolean = false,
    val isSaving: Boolean = false,
    val saveError: Boolean = false,
    val saved: Boolean = false,
) : UiState {
    val province: AreaMapVO? get() = regions.firstOrNull { it.area.code == provinceCode }
    val visibleRegions: List<AreaMapVO> get() =
        regions.filter {
            if (provinceCode == null) {
                it.area.level == AreaLevel.SIDO
            } else {
                it.area.level == AreaLevel.SIGUNGU && it.area.code.startsWith(provinceCode)
            }
        }
    val canContinue: Boolean get() = selected.isNotEmpty() && !isLoading && !loadError && !isSaving
}

sealed interface RegionMapEvent : ReducerEvent {
    data object Loading : RegionMapEvent

    data class Loaded(
        val regions: List<AreaMapVO>,
        val selected: List<AreaVO>,
    ) : RegionMapEvent

    data object LoadFailed : RegionMapEvent

    data class Browsed(
        val code: String?,
    ) : RegionMapEvent

    data class Toggled(
        val area: AreaVO,
    ) : RegionMapEvent

    data class Removed(
        val code: String,
    ) : RegionMapEvent

    data object Saving : RegionMapEvent

    data object Saved : RegionMapEvent

    data object SaveFailed : RegionMapEvent
}

@HiltViewModel
class RegionMapViewModel
    @Inject
    constructor(
        private val getAreaMap: GetAreaMapUseCase,
        private val getMyProfile: GetMyProfileUseCase,
        private val saveInterests: SaveInterestsUseCase,
    ) : MviViewModel<RegionMapIntent, RegionMapUiState, RegionMapEvent>(RegionMapUiState()) {
        private var initialization: RegionMapIntent.Initialize? = null

        override fun onIntent(intent: RegionMapIntent) {
            when (intent) {
                is RegionMapIntent.Initialize -> {
                    if (initialization == null) {
                        initialization = intent
                        load()
                    }
                }

                RegionMapIntent.Retry -> {
                    if (currentState.loadError) load()
                }

                is RegionMapIntent.Browse -> {
                    browse(intent.code)
                }

                is RegionMapIntent.Toggle -> {
                    toggle(intent.code)
                }

                is RegionMapIntent.Remove -> {
                    if (!currentState.isSaving) dispatch(RegionMapEvent.Removed(intent.code))
                }

                RegionMapIntent.Save -> {
                    save()
                }
            }
        }

        private fun browse(code: String?) {
            if (code == null || currentState.regions.any { it.area.code == code && it.area.level == AreaLevel.SIDO }) {
                dispatch(RegionMapEvent.Browsed(code))
            }
        }

        private fun toggle(code: String) {
            if (currentState.isLoading || currentState.loadError || currentState.isSaving) return
            currentState.regions.firstOrNull { it.area.code == code }?.let {
                dispatch(RegionMapEvent.Toggled(it.area))
            }
        }

        override fun reduce(
            state: RegionMapUiState,
            event: RegionMapEvent,
        ): RegionMapUiState =
            when (event) {
                RegionMapEvent.Loading -> {
                    state.copy(isLoading = true, loadError = false)
                }

                is RegionMapEvent.Loaded -> {
                    state.copy(
                        regions = event.regions.toImmutableList(),
                        selected = event.selected.toImmutableList(),
                        isLoading = false,
                        loadError = false,
                    )
                }

                RegionMapEvent.LoadFailed -> {
                    state.copy(isLoading = false, loadError = true)
                }

                is RegionMapEvent.Browsed -> {
                    state.copy(provinceCode = event.code, selectionFocusCode = null)
                }

                is RegionMapEvent.Toggled -> {
                    state.copy(
                        selected = toggleRegion(state.selected, event.area, state.regions).toImmutableList(),
                        selectionFocusCode = event.area.code,
                        saveError = false,
                    )
                }

                is RegionMapEvent.Removed -> {
                    state.copy(
                        selected = state.selected.filterNot { it.code == event.code }.toImmutableList(),
                        saveError = false,
                    )
                }

                RegionMapEvent.Saving -> {
                    state.copy(isSaving = true, saveError = false)
                }

                RegionMapEvent.Saved -> {
                    state.copy(isSaving = false, saved = true)
                }

                RegionMapEvent.SaveFailed -> {
                    state.copy(isSaving = false, saveError = true)
                }
            }

        private fun load() {
            val initial = initialization ?: return
            dispatch(RegionMapEvent.Loading)
            viewModelScope.launch {
                try {
                    val regions = getAreaMap()
                    require(regions.isNotEmpty())
                    val selected = if (initial.editing) getMyProfile().interestRegions else initial.regions
                    dispatch(RegionMapEvent.Loaded(regions, normalizeRegions(selected)))
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    // 저장된 선택을 읽지 못했을 때 빈 목록으로 덮어쓰지 않는다.
                    dispatch(RegionMapEvent.LoadFailed)
                }
            }
        }

        private fun save() {
            if (initialization?.editing != true || !currentState.canContinue) return
            val selected = currentState.selected
            dispatch(RegionMapEvent.Saving)
            viewModelScope.launch {
                try {
                    saveInterests.saveRegions(selected)
                    dispatch(RegionMapEvent.Saved)
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    // 서버 실패를 저장 성공으로 처리하지 않고 선택값과 화면을 유지한다.
                    dispatch(RegionMapEvent.SaveFailed)
                }
            }
        }
    }

/** 전체 범위는 한 항목으로 유지하고, 하위 지역을 누르면 그 지역만 제외한다. */
internal fun toggleRegion(
    selected: List<AreaVO>,
    area: AreaVO,
    regions: List<AreaMapVO>,
): List<AreaVO> {
    if (selected.any { it.code == area.code }) return selected.filterNot { it.code == area.code }
    val parent = selected.firstOrNull { area.code.startsWith(it.code) }
    if (parent != null) {
        val remaining =
            regions.map { it.area }.filter {
                it.level == AreaLevel.SIGUNGU && it.code.startsWith(parent.code) && it.code != area.code
            }
        return selected.filterNot { it.code == parent.code } + remaining
    }
    val updated = selected.filterNot { it.code.startsWith(area.code) } + area
    return collapseProvince(updated, area.code.take(2), regions)
}

/** 모든 시·군·구를 다시 포함하면 화면·캐시에서는 시·도 전체 한 항목으로 합친다. */
private fun collapseProvince(
    selected: List<AreaVO>,
    provinceCode: String,
    regions: List<AreaMapVO>,
): List<AreaVO> {
    val province = regions.firstOrNull { it.area.code == provinceCode }?.area ?: return selected
    val children =
        regions.map { it.area }.filter {
            it.level == AreaLevel.SIGUNGU && it.code.startsWith(provinceCode)
        }
    if (children.isEmpty() || !children.all { child -> selected.any { it.code == child.code } }) return selected
    return selected.filterNot { it.code.startsWith(provinceCode) } + province
}

/** 기존 동 단위 선택은 보존한다. 서버 정규형(10자리 0 패딩)도 prefix로 비교한다. */
internal fun normalizeRegions(regions: List<AreaVO>): List<AreaVO> =
    buildList {
        regions
            .mapNotNull { area ->
                val code = area.code
                if (code.length !in listOf(2, 5, 10) || !code.all(Char::isDigit)) return@mapNotNull null
                when {
                    code.length == 2 || (
                        code.length == 10 &&
                            code.drop(2).all {
                                it == '0'
                            }
                    ) -> area.copy(code = code.take(2), level = AreaLevel.SIDO)

                    code.length == 5 || (
                        code.length == 10 &&
                            code.drop(5).all {
                                it == '0'
                            }
                    ) -> area.copy(code = code.take(5), level = AreaLevel.SIGUNGU)

                    else -> area
                }
            }.sortedBy { it.code.length }
            .forEach { area ->
                if (none { area.code.startsWith(it.code) }) add(area)
            }
    }

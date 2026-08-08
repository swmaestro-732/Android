package com.chillsam.courmy.main.presentation.my

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviIntent
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.common.presentation.mvi.ReducerEvent
import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.main.domain.area.SearchAreasUseCase
import com.chillsam.courmy.main.entity.area.AreaVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 글자를 칠 때마다 부르지 않도록 입력이 멎기를 기다리는 시간(ms). */
private const val SEARCH_DEBOUNCE_MS = 250L

sealed interface AreaSearchIntent : MviIntent {
    /** 검색어 입력. 잠시 멎으면 자동으로 검색한다. */
    data class QueryChanged(
        val keyword: String,
    ) : AreaSearchIntent

    /** 인기 지역 탭. 이름으로 검색해 실제 행정구역으로 바꾼 뒤 [AreaSearchUiState.resolved] 로 돌려준다. */
    data class ResolvePopular(
        val name: String,
    ) : AreaSearchIntent

    data object ConsumeResolved : AreaSearchIntent
}

data class AreaSearchUiState(
    val keyword: String = "",
    val results: List<AreaVO> = emptyList(),
    val isSearching: Boolean = false,
    /** 서버 검색이 실패해 내장 목록으로 대신 채운 상태. 화면이 안내 문구를 띄우는 데 쓴다. */
    val usedFallback: Boolean = false,
    /** 인기 지역 탭이 실제 행정구역으로 풀린 1회성 결과. 화면이 선택에 담고 비운다. */
    val resolved: AreaVO? = null,
) : UiState {
    companion object {
        val empty = AreaSearchUiState()
    }
}

sealed interface AreaSearchReducerEvent : ReducerEvent {
    data class QueryChanged(
        val keyword: String,
    ) : AreaSearchReducerEvent

    data object Started : AreaSearchReducerEvent

    data class Loaded(
        val results: List<AreaVO>,
        val usedFallback: Boolean,
    ) : AreaSearchReducerEvent

    data class Resolved(
        val area: AreaVO,
    ) : AreaSearchReducerEvent

    data object ResolvedConsumed : AreaSearchReducerEvent
}

/**
 * 관심 지역 검색 ViewModel(`GET /api/v1/areas/search`).
 *
 * 서버 호출이 실패하면 내장 목록([FALLBACK_AREAS])을 걸러 대신 보여 준다.
 * 엔드포인트가 아직 배포되지 않았을 수 있어(백엔드 main 에는 머지됨) 화면이 비어 보이는 걸 막기 위해서다.
 * 폴백 항목은 법정동코드가 없어 [AreaVO.code] 가 빈 문자열이며, 회원가입 전송 시 걸러진다. [wiki-needed]
 */
@HiltViewModel
class AreaSearchViewModel
    @Inject
    constructor(
        private val searchAreasUseCase: SearchAreasUseCase,
    ) : MviViewModel<AreaSearchIntent, AreaSearchUiState, AreaSearchReducerEvent>(AreaSearchUiState.empty) {
        private var job: Job? = null

        override fun onIntent(intent: AreaSearchIntent) {
            when (intent) {
                is AreaSearchIntent.QueryChanged -> search(intent.keyword)
                is AreaSearchIntent.ResolvePopular -> resolvePopular(intent.name)
                AreaSearchIntent.ConsumeResolved -> dispatch(AreaSearchReducerEvent.ResolvedConsumed)
            }
        }

        override fun reduce(
            state: AreaSearchUiState,
            event: AreaSearchReducerEvent,
        ): AreaSearchUiState =
            when (event) {
                is AreaSearchReducerEvent.QueryChanged -> {
                    state.copy(keyword = event.keyword)
                }

                AreaSearchReducerEvent.Started -> {
                    state.copy(isSearching = true)
                }

                is AreaSearchReducerEvent.Loaded -> {
                    state.copy(
                        results = event.results,
                        isSearching = false,
                        usedFallback = event.usedFallback,
                    )
                }

                is AreaSearchReducerEvent.Resolved -> {
                    state.copy(resolved = event.area)
                }

                AreaSearchReducerEvent.ResolvedConsumed -> {
                    state.copy(resolved = null)
                }
            }

        private fun search(keyword: String) {
            dispatch(AreaSearchReducerEvent.QueryChanged(keyword))
            job?.cancel()
            if (keyword.isBlank()) {
                dispatch(AreaSearchReducerEvent.Loaded(emptyList(), usedFallback = false))
                return
            }
            dispatch(AreaSearchReducerEvent.Started)
            job =
                viewModelScope.launch {
                    delay(SEARCH_DEBOUNCE_MS)
                    runCatching { searchAreasUseCase(keyword) }
                        .onSuccess { dispatch(AreaSearchReducerEvent.Loaded(it, usedFallback = false)) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "지역 검색 실패, 내장 목록으로 대체: ${e.message}", e)
                            dispatch(AreaSearchReducerEvent.Loaded(fallbackAreas(keyword), usedFallback = true))
                        }
                }
        }

        /**
         * 인기 지역 이름을 실제 행정구역 한 건으로 바꾼다.
         *
         * 칩에는 법정동코드가 없어 그대로 고르면 서버에 보낼 수 없다. 그래서 이름으로 한 번 검색해
         * 코드가 붙은 결과를 집는다. 이름이 그대로 걸리는 결과를 먼저 보고(예: "성수동" → "성수동1가"),
         * 없으면 첫 결과를, 검색 자체가 비면 코드 없는 값으로라도 골라 화면이 무반응이 되지 않게 한다.
         */
        private fun resolvePopular(name: String) {
            job?.cancel()
            job =
                viewModelScope.launch {
                    val candidates =
                        runCatching { searchAreasUseCase(name) }
                            .getOrElse { e ->
                                if (e is CancellationException) throw e
                                Log.w(TAG, "인기 지역 조회 실패, 내장 목록으로 대체: ${e.message}", e)
                                fallbackAreas(name)
                            }
                    val area =
                        candidates.firstOrNull { it.shortName.contains(name) || it.fullName.contains(name) }
                            ?: candidates.firstOrNull()
                            ?: AreaVO(code = "", shortName = name, fullName = name)
                    dispatch(AreaSearchReducerEvent.Resolved(area))
                }
        }

        private companion object {
            const val TAG = "AreaSearch"
        }
    }

/**
 * 서버 검색이 안 될 때 쓰는 내장 지역 목록.
 *
 * 법정동코드가 없어 [AreaVO.code] 를 비워 둔다 — 화면에서 고르고 기기에 저장하는 것까지는 되지만
 * 회원가입 요청의 `areaCodes` 에는 실리지 않는다. 서버 검색이 자리 잡으면 이 목록을 지운다.
 */
private val FALLBACK_AREAS =
    listOf(
        "서울특별시 성동구 성수동" to "성수동",
        "서울특별시 마포구 연남동" to "연남동",
        "서울특별시 마포구 합정동" to "합정동",
        "서울특별시 마포구 망원동" to "망원동",
        "서울특별시 용산구 한남동" to "한남동",
        "서울특별시 용산구 이태원동" to "이태원동",
        "서울특별시 종로구 익선동" to "익선동",
        "서울특별시 종로구 삼청동" to "삼청동",
        "서울특별시 종로구 사직동" to "서촌",
        "서울특별시 중구 을지로동" to "을지로",
        "서울특별시 마포구 서교동" to "홍대",
        "서울특별시 송파구 잠실동" to "잠실",
        "서울특별시 강남구 압구정동" to "압구정",
    ).map { (full, short) -> AreaVO(code = "", shortName = short, fullName = full) }

private fun fallbackAreas(keyword: String): List<AreaVO> =
    FALLBACK_AREAS.filter { it.shortName.contains(keyword) || it.fullName.contains(keyword) }

package com.chillsam.courmy.course.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.course.domain.GetPlaceDetailUseCase
import com.chillsam.courmy.course.entity.PlaceDetailVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 코스 상세에서 장소를 탭했을 때 여는 장소 상세 시트의 상태.
 *
 * [place] 가 있으면 시트를 렌더하고, 없으면 [isLoading]/[errorMessage] 로 분기한다.
 * 시트가 닫히면 [clear] 로 비워, 다음에 다른 장소를 열 때 이전 내용이 남지 않게 한다.
 */
data class PlaceDetailUiState(
    val isLoading: Boolean = false,
    val place: PlaceDetailVO? = null,
    val errorMessage: String? = null,
)

/**
 * 장소 상세 시트 ViewModel. `GET /service/v1/places/{placeId}` 를 [GetPlaceDetailUseCase] 로 로드한다.
 *
 * 코스 상세 화면 안의 보조 시트라 MVI 계약(Intent/Reducer) 대신 단순 상태 홀더로 둔다
 * (입력이 "열기/닫기" 둘뿐이고 화면 상태와 독립적이다).
 */
@HiltViewModel
class PlaceDetailViewModel
    @Inject
    constructor(
        private val getPlaceDetailUseCase: GetPlaceDetailUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(PlaceDetailUiState())
        val uiState: StateFlow<PlaceDetailUiState> = _uiState

        private var loadJob: Job? = null

        fun open(
            placeId: Long,
            walkText: String,
        ) {
            loadJob?.cancel()
            // 장소 id 가 없으면(응답에 placeId 가 빠진 경우) 서버에 물어볼 것이 없다.
            if (placeId <= 0L) {
                _uiState.value = PlaceDetailUiState(errorMessage = "장소 정보를 찾을 수 없습니다.")
                return
            }
            // TODO-API-SPEC: 백엔드 PlaceDetailScreenController 가 아직 목이라 placeId=101 만 200 이고
            // 나머지는 404 다. 실제 코스의 장소를 눌러도 시트를 볼 수 있도록 임시로 목 id 로 고정한다.
            // 실제 조회로 교체되면 이 상수와 아래 치환을 지우고 placeId 를 그대로 넘긴다. [wiki-needed]
            val requestedId = MOCK_PLACE_ID
            _uiState.value = PlaceDetailUiState(isLoading = true)
            loadJob =
                viewModelScope.launch {
                    runCatching { getPlaceDetailUseCase(requestedId, walkText) }
                        .onSuccess { place ->
                            _uiState.value = PlaceDetailUiState(place = place)
                        }.onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "장소 상세 로드 실패: placeId=$requestedId(요청) / $placeId(실제)", e)
                            _uiState.value = PlaceDetailUiState(errorMessage = "장소를 불러오지 못했습니다.")
                        }
                }
        }

        fun clear() {
            loadJob?.cancel()
            _uiState.value = PlaceDetailUiState()
        }

        private companion object {
            const val TAG = "PlaceDetail"

            /** 백엔드 목이 유일하게 200 을 주는 장소 id. 실제 조회 구현 시 제거한다. */
            const val MOCK_PLACE_ID = 101L
        }
    }

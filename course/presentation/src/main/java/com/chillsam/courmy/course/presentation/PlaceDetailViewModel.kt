package com.chillsam.courmy.course.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.course.domain.GetPlaceDetailUseCase
import com.chillsam.courmy.course.domain.GetPlaceReviewsUseCase
import com.chillsam.courmy.course.entity.CourseReviewListVO
import com.chillsam.courmy.course.entity.PlaceDetailVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 코스 상세에서 장소를 탭했을 때 여는 장소 상세 시트의 상태.
 *
 * [place] 가 있으면 시트를 렌더하고, 없으면 [isLoading]/[errorMessage] 로 분기한다.
 * 시트가 닫히면 [clear] 로 비워, 다음에 다른 장소를 열 때 이전 내용이 남지 않게 한다.
 *
 * [reviews] 는 장소 후기다(코스 후기와 **다른 데이터**, 같은 응답 구조). 못 받아도 시트 본체는
 * 멀쩡하므로 null 이면 후기 섹션만 빈 상태로 그린다.
 *
 * [isSaved] 는 장소 저장 토글의 화면 세션 상태다.
 * TODO-API-SPEC: `POST/DELETE /api/v1/saved-places` 연동 전까지는 서버에 반영되지 않는다.
 */
data class PlaceDetailUiState(
    val isLoading: Boolean = false,
    val place: PlaceDetailVO? = null,
    val errorMessage: String? = null,
    val reviews: CourseReviewListVO? = null,
    val isSaved: Boolean = false,
)

/**
 * 장소 상세 시트 ViewModel. `GET /service/v1/places/{placeId}` 와
 * `GET /service/v1/places/{placeId}/reviews` 를 로드한다.
 *
 * 코스 상세 화면 안의 보조 시트라 MVI 계약(Intent/Reducer) 대신 단순 상태 홀더로 둔다
 * (입력이 "열기/닫기/저장 토글" 뿐이고 화면 상태와 독립적이다).
 */
@HiltViewModel
class PlaceDetailViewModel
    @Inject
    constructor(
        private val getPlaceDetailUseCase: GetPlaceDetailUseCase,
        private val getPlaceReviewsUseCase: GetPlaceReviewsUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(PlaceDetailUiState())
        val uiState: StateFlow<PlaceDetailUiState> = _uiState

        private var loadJob: Job? = null
        private var reviewsJob: Job? = null

        fun open(placeId: Long) {
            loadJob?.cancel()
            reviewsJob?.cancel()
            // 장소 id 가 없으면(응답에 placeId 가 빠진 경우) 서버에 물어볼 것이 없다.
            if (placeId <= 0L) {
                _uiState.value = PlaceDetailUiState(errorMessage = "주소를 불러오지 못했어요.")
                return
            }
            _uiState.value = PlaceDetailUiState(isLoading = true)
            loadJob =
                viewModelScope.launch {
                    runCatching { getPlaceDetailUseCase(placeId) }
                        .onSuccess { place ->
                            _uiState.update { it.copy(isLoading = false, place = place) }
                        }.onFailure { e ->
                            if (e is CancellationException) throw e
                            Log.w(TAG, "장소 상세 로드 실패: placeId=$placeId", e)
                            // 시트는 코스 데이터로 이미 그려져 있고 이 응답에서 쓰는 건 주소뿐이다.
                            _uiState.update { it.copy(isLoading = false, errorMessage = "주소를 불러오지 못했어요.") }
                        }
                }
            // 후기는 상세와 독립 호출이라 따로 받는다 — 하나가 늦거나 실패해도 서로 막지 않는다.
            reviewsJob =
                viewModelScope.launch {
                    runCatching { getPlaceReviewsUseCase(placeId) }
                        .onSuccess { reviews ->
                            _uiState.update { it.copy(reviews = reviews) }
                        }.onFailure { e ->
                            if (e is CancellationException) throw e
                            // 후기 없이도 시트는 성립한다. 섹션이 빈 상태로 남을 뿐이라 토스트도 띄우지 않는다.
                            Log.w(TAG, "장소 후기 로드 실패: placeId=$placeId", e)
                        }
                }
        }

        /** 저장 토글. TODO-API-SPEC: saved-places 연동 전까지 화면 세션 상태만 바꾼다. */
        fun toggleSave() {
            _uiState.update { it.copy(isSaved = !it.isSaved) }
        }

        fun clear() {
            loadJob?.cancel()
            reviewsJob?.cancel()
            _uiState.value = PlaceDetailUiState()
        }

        private companion object {
            const val TAG = "PlaceDetail"
        }
    }

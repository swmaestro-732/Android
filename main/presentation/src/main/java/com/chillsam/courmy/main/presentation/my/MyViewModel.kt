package com.chillsam.courmy.main.presentation.my

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.mvi.MviViewModel
import com.chillsam.courmy.main.domain.my.GetMyProfileUseCase
import com.chillsam.courmy.main.entity.my.MyProfileVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 마이·프로필(FS-15) ViewModel. `service/v1/my/profile`(BFF)을 [GetMyProfileUseCase] 로 로드하고
 * 로딩/성공/실패를 [MyProfileUIState] 로 노출한다(에러 시 [MyProfileIntent.Retry]).
 */
@HiltViewModel
class MyViewModel
    @Inject
    constructor(
        private val getMyProfileUseCase: GetMyProfileUseCase,
    ) : MviViewModel<MyProfileIntent, MyProfileUIState, MyProfileReducerEvent>(
            MyProfileUIState.empty,
        ) {
        private var loadJob: Job? = null

        init {
            onIntent(MyProfileIntent.Load)
        }

        override fun onIntent(intent: MyProfileIntent) {
            when (intent) {
                MyProfileIntent.Load,
                MyProfileIntent.Retry,
                -> load()
            }
        }

        override fun reduce(
            state: MyProfileUIState,
            event: MyProfileReducerEvent,
        ): MyProfileUIState =
            when (event) {
                MyProfileReducerEvent.LoadStarted -> {
                    state.copy(isLoading = true, errorMessage = null)
                }

                is MyProfileReducerEvent.Loaded -> {
                    state.copy(isLoading = false, profile = event.profile, errorMessage = null)
                }

                is MyProfileReducerEvent.Failed -> {
                    // 실패 시 이전 프로필을 비워, stale 데이터가 에러 화면을 가리지 않게 한다.
                    state.copy(isLoading = false, profile = null, errorMessage = event.message)
                }
            }

        private fun load() {
            // 개발용: 백엔드 `service/v1/my/profile` 미연동 상태라 기본 true 로 더미를 즉시 노출한다.
            // 실 연동 시 false 로 바꾸면 아래 실제 API 경로(로딩→성공/에러+재시도)를 탄다.
            if (USE_SAMPLE) {
                dispatch(MyProfileReducerEvent.Loaded(MyProfileVO.sample))
                return
            }
            dispatch(MyProfileReducerEvent.LoadStarted)
            loadJob?.cancel()
            loadJob =
                viewModelScope.launch {
                    runCatching { getMyProfileUseCase() }
                        .onSuccess { profile -> dispatch(MyProfileReducerEvent.Loaded(profile)) }
                        .onFailure { e ->
                            if (e is CancellationException) throw e
                            // 원문 예외 메시지는 로그로만 남기고, UI 에는 안정적인 문구를 노출한다.
                            Log.w(TAG, "마이 프로필 로드 실패", e)
                            dispatch(MyProfileReducerEvent.Failed("프로필을 불러오지 못했습니다."))
                        }
                }
        }

        companion object {
            private const val TAG = "MyViewModel"

            /** 개발용 더미 토글. 실제 API 연동 시 false 로 바꾼다. */
            const val USE_SAMPLE = true
        }
    }

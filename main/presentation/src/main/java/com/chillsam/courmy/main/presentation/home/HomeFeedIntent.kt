package com.chillsam.courmy.main.presentation.home

import com.chillsam.courmy.common.presentation.mvi.MviIntent

/** 홈 공개 코스 피드(FS-09) 사용자 입력. */
sealed interface HomeFeedIntent : MviIntent {
    data object Load : HomeFeedIntent

    data object Retry : HomeFeedIntent
}

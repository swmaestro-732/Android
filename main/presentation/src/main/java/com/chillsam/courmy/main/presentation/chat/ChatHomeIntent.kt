package com.chillsam.courmy.main.presentation.chat

import com.chillsam.courmy.common.presentation.mvi.MviIntent

sealed interface ChatHomeIntent : MviIntent {
    /** 화면 진입 시 목록을 불러온다. */
    data object Load : ChatHomeIntent

    /** 상단 탭 전환(참여 중 / 동네 채팅방). */
    data class SelectTab(
        val tab: ChatHomeTab,
    ) : ChatHomeIntent
}

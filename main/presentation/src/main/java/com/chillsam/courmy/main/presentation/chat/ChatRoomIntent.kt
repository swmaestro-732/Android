package com.chillsam.courmy.main.presentation.chat

import com.chillsam.courmy.common.presentation.mvi.MviIntent

sealed interface ChatRoomIntent : MviIntent {
    /** 방 진입 시 해당 대화를 불러온다. */
    data class Load(
        val roomId: Long,
    ) : ChatRoomIntent

    /** 입력창 문구 변경. */
    data class DraftChanged(
        val value: String,
    ) : ChatRoomIntent

    /** 현재 입력 문구를 내 메시지로 전송. */
    data object Send : ChatRoomIntent
}

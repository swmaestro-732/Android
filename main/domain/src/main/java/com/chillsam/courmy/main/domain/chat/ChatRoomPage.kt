package com.chillsam.courmy.main.domain.chat

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 채팅방 화면. 채팅홈([ChatPage]) 목록에서 대화를 눌러 진입하며, roomId 로 대화를 식별한다. */
object ChatRoomPage : Page {
    const val PATH = "/chat/room"
    const val ARG_ROOM_ID = "roomId"

    override fun toRoute(): NavRoute = NavRoute(PATH)

    fun route(roomId: Long): NavRoute = NavRoute(PATH, mapOf(ARG_ROOM_ID to roomId.toString()))
}

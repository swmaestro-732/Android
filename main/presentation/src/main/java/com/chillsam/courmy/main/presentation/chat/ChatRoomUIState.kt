package com.chillsam.courmy.main.presentation.chat

import androidx.compose.runtime.Immutable
import com.chillsam.courmy.common.presentation.mvi.UiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 말풍선 한 개.
 * [fromMe] 가 true 면 내가 보낸 메시지(오른쪽 정렬·강조색).
 * [senderName] 은 그룹 채팅에서 상대 말풍선 위에 보일 보낸 사람 이름(1:1·내 메시지는 null).
 */
@Immutable
data class ChatMessageUi(
    val id: Long,
    val content: String,
    val fromMe: Boolean,
    val time: ChatTime,
    val senderName: String? = null,
)

/**
 * 채팅방 화면 상태.
 *
 * 입력 중인 문구는 [draft] 에 담고, 전송 시 [messages] 끝에 붙인다.
 * [subtitle] 은 상단 제목 아래 부제(예: "연남동 · 12명"), 1:1 방이면 비어 있다.
 * 서버 연동 전이라 [messages] 는 [ChatSampleData] 로 채운다.
 */
data class ChatRoomUIState(
    val roomTitle: String = "",
    val subtitle: String = "",
    val messages: ImmutableList<ChatMessageUi> = persistentListOf(),
    val draft: String = "",
) : UiState {
    val canSend: Boolean get() = draft.isNotBlank()

    companion object {
        val empty: ChatRoomUIState = ChatRoomUIState()
    }
}

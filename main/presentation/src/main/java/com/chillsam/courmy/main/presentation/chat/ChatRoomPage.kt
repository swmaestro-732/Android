package com.chillsam.courmy.main.presentation.chat

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * 채팅방 라우팅 진입점. 채팅홈([ChatPage]) 목록에서 대화를 눌러 들어온다.
 *
 * 탭 루트가 아니라 뒤로가기로 빠져나가는 하위 화면이라 하단 탭바를 두지 않는다.
 * 입력창이 키보드 위로 올라오도록 [imePadding] 을 준다.
 */
@Composable
fun ChatRoomPage(
    roomId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatRoomViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(roomId) { viewModel.onIntent(ChatRoomIntent.Load(roomId)) }

    ChatRoomScreen(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        modifier = modifier.fillMaxSize().imePadding(),
    )
}

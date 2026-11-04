package com.chillsam.courmy.main.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/** 채팅방 UI: 뒤로가기 상단바(제목·부제) + 말풍선 목록 + 하단 입력창. 네비게이션/ViewModel 을 모르는 순수 표시 레이어. */
@Composable
internal fun ChatRoomScreen(
    uiState: ChatRoomUIState,
    onIntent: (ChatRoomIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val listState = rememberLazyListState()

    // 메시지가 늘면 항상 마지막 말풍선이 보이도록 끝으로 스크롤한다(전송 직후·최초 로딩 포함).
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.lastIndex)
        }
    }

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel1)) {
        ChatRoomTopBar(title = uiState.roomTitle, subtitle = uiState.subtitle, onBack = onBack)
        HorizontalDivider(thickness = 1.dp, color = color.borderDefaultLevel0)
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth().background(color.bgDefaultLevel0),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(uiState.messages, key = { it.id }) { message ->
                ChatBubble(message = message)
            }
        }
        ChatInputBar(
            value = uiState.draft,
            onValueChange = { onIntent(ChatRoomIntent.DraftChanged(it)) },
            onSend = { onIntent(ChatRoomIntent.Send) },
        )
    }
}

@Composable
private fun ChatRoomTopBar(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(color.bgDefaultLevel1)
                .statusBarsPadding()
                .height(56.dp)
                .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.align(Alignment.CenterStart).size(44.dp).clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_left_24),
                contentDescription = "뒤로 가기",
                tint = color.contentDefaultLevel0,
                modifier = Modifier.size(24.dp),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            DsText(
                text = title,
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
            )
            if (subtitle.isNotEmpty()) {
                Spacer(Modifier.height(2.dp))
                DsText(
                    text = subtitle,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel2,
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessageUi) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.fromMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top,
    ) {
        if (!message.fromMe) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(color.imagePlaceholder),
            )
            Spacer(Modifier.width(8.dp))
        }
        Column(
            horizontalAlignment = if (message.fromMe) Alignment.End else Alignment.Start,
        ) {
            if (!message.fromMe && message.senderName != null) {
                DsText(
                    text = message.senderName,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel2,
                )
                Spacer(Modifier.height(4.dp))
            }
            val bubbleShape =
                if (message.fromMe) {
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
                } else {
                    RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
                }
            Box(
                modifier =
                    Modifier
                        .widthIn(max = 264.dp)
                        .clip(bubbleShape)
                        .background(if (message.fromMe) color.bgAccent else color.bgDefaultLevel1)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                DsText(
                    text = message.content,
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = if (message.fromMe) color.contentOnAccent else color.contentDefaultLevel0,
                    maxLines = 30,
                    softWrap = true,
                )
            }
            Spacer(Modifier.height(4.dp))
            DsText(
                text = message.time,
                style = DesignSystemThemeImpl.typeScale.textExtraXS,
                color = color.contentDefaultLevel2,
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(modifier = Modifier.background(color.bgDefaultLevel1)) {
        HorizontalDivider(thickness = 1.dp, color = color.borderDefaultLevel0)
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(modifier = Modifier.weight(1f)) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(color.bgDefaultLevel0)
                            .padding(horizontal = 18.dp, vertical = 11.dp),
                    textStyle =
                        DesignSystemThemeImpl.typeScale.textRegularS
                            .copy(color = color.contentDefaultLevel0),
                    cursorBrush = SolidColor(color.contentAccent),
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSend() }),
                )
                if (value.isEmpty()) {
                    DsText(
                        text = "메시지 보내기",
                        style = DesignSystemThemeImpl.typeScale.textRegularS,
                        color = color.contentDefaultLevel2,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 11.dp),
                    )
                }
            }
            Box(
                modifier =
                    Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(color.bgAccent)
                        .clickable(onClick = onSend),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_send_24),
                    contentDescription = "전송",
                    tint = color.contentOnAccent,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

package com.chillsam.courmy.main.presentation.aichat

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsChip
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.DsTextField
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.presentation.component.BackTopBar

private val ScreenPadding = 16.dp
private val BubbleMaxWidth = 280.dp
private val AvatarSize = 32.dp

/**
 * 커미 AI 코스 추천 채팅 화면.
 *
 * 홈의 "커미 AI" 배너에서 진입한다. 화면이 보일 때 [AiCourseChatIntent.Start] 를 보내 첫 인사를 띄운다.
 */
@Composable
fun AiCourseChatPage(
    onBack: () -> Unit,
    viewModel: AiCourseChatViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.onIntent(AiCourseChatIntent.Start)
    }
    AiCourseChatScreen(
        state = state,
        onBack = onBack,
        onSend = { viewModel.onIntent(AiCourseChatIntent.SendText(it)) },
        onQuickReply = { viewModel.onIntent(AiCourseChatIntent.QuickReply(it)) },
    )
}

@Composable
private fun AiCourseChatScreen(
    state: AiCourseChatUIState,
    onBack: () -> Unit,
    onSend: (String) -> Unit,
    onQuickReply: (String) -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(color.bgDefaultLevel0)
                .statusBarsPadding(),
    ) {
        BackTopBar(title = "커미 AI", onBack = onBack)

        val listState = rememberLazyListState()
        // 새 말풍선·입력 중 표시가 생기면 항상 마지막 줄이 보이도록 바닥으로 스크롤한다.
        androidx.compose.runtime.LaunchedEffect(state.messages.size, state.isAiTyping) {
            val count = state.messages.size + if (state.isAiTyping) 1 else 0
            if (count > 0) listState.animateScrollToItem(count - 1)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().weight(1f),
            state = listState,
            contentPadding = PaddingValues(horizontal = ScreenPadding, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(state.messages, key = { it.id }) { message ->
                MessageRow(message)
            }
            if (state.isAiTyping) {
                item(key = "typing") { TypingRow() }
            }
        }

        ChatInputBar(
            quickReplies = state.quickReplies,
            enabled = state.inputEnabled,
            onSend = onSend,
            onQuickReply = onQuickReply,
        )
    }
}

@Composable
private fun MessageRow(message: AiChatMessage) {
    when (message.role) {
        ChatRole.AI -> AiMessageRow(message)
        ChatRole.USER -> UserMessageRow(message)
    }
}

@Composable
private fun AiMessageRow(message: AiChatMessage) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top,
    ) {
        AiAvatar()
        Spacer(Modifier.width(8.dp))
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .widthIn(max = BubbleMaxWidth)
                        .clip(
                            RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
                        ).background(color.bgDefaultLevel1)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                DsText(
                    text = message.text,
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentDefaultLevel0,
                    maxLines = Int.MAX_VALUE,
                )
            }
            message.recommendation?.let { RecommendationCard(it) }
        }
    }
}

@Composable
private fun UserMessageRow(message: AiChatMessage) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        Box(
            modifier =
                Modifier
                    .widthIn(max = BubbleMaxWidth)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomEnd = 16.dp, bottomStart = 16.dp))
                    .background(color.bgAccent)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            DsText(
                text = message.text,
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentOnAccent,
                maxLines = Int.MAX_VALUE,
            )
        }
    }
}

@Composable
private fun AiAvatar() {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .size(AvatarSize)
                .clip(CircleShape)
                .background(color.bgAccentSubtle),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_sparkle_24),
            contentDescription = null,
            tint = color.contentAccent,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun RecommendationCard(recommendation: AiCourseRecommendation) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            Modifier
                .widthIn(max = BubbleMaxWidth)
                .clip(RoundedCornerShape(16.dp))
                .background(color.bgDefaultLevel1)
                .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // 썸네일 자리(목업이라 이미지 placeholder 톤만 채운다).
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.imagePlaceholder)
                    .padding(10.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .clip(CircleShape)
                        .background(color.bgDefaultLevel1)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                DsText(
                    text = recommendation.tagLabel,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel1,
                )
            }
            Spacer(Modifier.size(28.dp))
        }
        DsText(
            text = recommendation.title,
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentDefaultLevel0,
            maxLines = 2,
        )
        DsText(
            text = recommendation.meta,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel2,
        )
    }
}

@Composable
private fun TypingRow() {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(verticalAlignment = Alignment.Top) {
        AiAvatar()
        Spacer(Modifier.width(8.dp))
        Box(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp))
                    .background(color.bgDefaultLevel1)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                repeat(3) { index -> TypingDot(index) }
            }
        }
    }
}

@Composable
private fun TypingDot(index: Int) {
    val color = DesignSystemThemeImpl.designSystemColor
    val transition = rememberInfiniteTransition(label = "typing")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 600, delayMillis = index * 150),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "dot$index",
    )
    Box(
        modifier =
            Modifier
                .size(6.dp)
                .alpha(alpha)
                .clip(CircleShape)
                .background(color.contentDefaultLevel2),
    )
}

@Composable
private fun ChatInputBar(
    quickReplies: List<String>,
    enabled: Boolean,
    onSend: (String) -> Unit,
    onQuickReply: (String) -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    var input by remember { mutableStateOf("") }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(color.bgDefaultLevel0)
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = ScreenPadding, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (quickReplies.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                quickReplies.forEach { reply ->
                    DsChip(
                        text = reply,
                        selected = false,
                        enabled = enabled,
                        onClick = { if (enabled) onQuickReply(reply) },
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DsTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = "메시지를 입력하세요",
            )
            val canSend = enabled && input.isNotBlank()
            SendButton(
                enabled = canSend,
                onClick = {
                    onSend(input)
                    input = ""
                },
            )
        }
    }
}

@Composable
private fun SendButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val background = if (enabled) color.bgAccent else color.bgAccentSubtle
    val tint = if (enabled) color.contentOnAccent else color.contentDefaultLevel2
    Box(
        modifier =
            Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(background)
                .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_up_24),
            contentDescription = "보내기",
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
    }
}

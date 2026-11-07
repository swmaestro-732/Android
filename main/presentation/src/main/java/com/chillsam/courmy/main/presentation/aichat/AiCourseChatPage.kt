package com.chillsam.courmy.main.presentation.aichat

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.DsTextField
import com.chillsam.courmy.common.presentation.helper.StatusBarColor
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

private val ScreenPadding = 16.dp
private val BubbleMaxWidth = 272.dp
private val AvatarSize = 40.dp
private val GlassRadius = RoundedCornerShape(22.dp)
private val CardRadius = RoundedCornerShape(20.dp)

/**
 * 커미 AI 코스 추천 채팅 화면(파스텔 + 글래스).
 *
 * 몽환적 민트·초록 오로라 배경 위에 반투명 글래스 말풍선·카드가 떠 있다. 진입하면 인사 + 작은 선택 알약,
 * 문답(지역 → 테마 → 장소)을 거쳐 코스 3개를 추천한다. 결과는 대표+2([AiRecLayout.FEATURED])로 보여 준다.
 */
@Composable
fun AiCourseChatPage(
    onBack: () -> Unit,
    viewModel: AiCourseChatViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.onIntent(AiCourseChatIntent.Start) }
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
    // 상태바 스크림을 투명(밝은 RGB+알파0)으로 선언해 오로라가 상태바 뒤까지 비치게 한다.
    // RGB 가 밝아 시스템 아이콘은 어둡게 유지된다(RootComposable 의 휘도 판정).
    StatusBarColor(AiChatPalette.auroraBase.copy(alpha = 0f))
    val drift = rememberAuroraDrift()
    val hasCards = state.quickReplies.isNotEmpty() && !state.isAiTyping
    // 오로라는 상태바 뒤까지 꽉 채우고(바깥 Box), 콘텐츠만 상태바 여백을 둔다(엣지-투-엣지).
    Box(modifier = Modifier.fillMaxSize().drawBehind { drawAurora(drift) }) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            ChatTopBar(onBack = onBack)

            val listState = rememberLazyListState()
            LaunchedEffect(state.messages.size, state.quickReplies.size, state.isAiTyping) {
                val extra = (if (hasCards) 1 else 0) + (if (state.isAiTyping) 1 else 0)
                val target = state.messages.size - 1 + extra
                if (target >= 0) listState.animateScrollToItem(target)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                state = listState,
                contentPadding = PaddingValues(horizontal = ScreenPadding, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                itemsIndexed(state.messages, key = { _, m -> m.id }) { _, message ->
                    MessageRow(message = message, layout = state.resultLayout)
                }
                if (hasCards) {
                    item(key = "cards") {
                        QuickReplyPills(
                            replies = state.quickReplies,
                            kind = state.quickReplyKind,
                            enabled = state.inputEnabled,
                            onQuickReply = onQuickReply,
                        )
                    }
                }
                if (state.isAiTyping) {
                    item(key = "typing") { TypingRow() }
                }
            }

            ChatInputBar(enabled = state.inputEnabled, onSend = onSend)
        }
    }
}

// ---------------- top bar ----------------

@Composable
private fun ChatTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(52.dp).padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_chevron_left_24),
            contentDescription = "뒤로",
            tint = AiChatPalette.ink,
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onBack)
                    .padding(8.dp),
        )
        DsText(
            text = "커미 AI",
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = AiChatPalette.ink,
        )
        DsText(
            text = "Beta",
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = AiChatPalette.inkSoft,
        )
    }
}

// ---------------- message rows ----------------

@Composable
private fun MessageRow(
    message: AiChatMessage,
    layout: AiRecLayout,
) {
    when (message.role) {
        ChatRole.AI -> AiMessageRow(message = message, layout = layout)
        ChatRole.USER -> UserMessageRow(message)
    }
}

@Composable
private fun AiMessageRow(
    message: AiChatMessage,
    layout: AiRecLayout,
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        AiAvatar()
        Spacer(Modifier.width(11.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            GlassBubble(message.text)
            if (message.recommendations.isNotEmpty()) {
                RecommendationBlock(recommendations = message.recommendations, layout = layout)
            }
        }
    }
}

@Composable
private fun GlassBubble(text: String) {
    Box(
        modifier =
            Modifier
                .widthIn(max = BubbleMaxWidth)
                .glass(RoundedCornerShape(topStart = 6.dp, topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 20.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = AiChatPalette.ink,
            maxLines = Int.MAX_VALUE,
        )
    }
}

@Composable
private fun UserMessageRow(message: AiChatMessage) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Box(
            modifier =
                Modifier
                    .widthIn(max = BubbleMaxWidth)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomEnd = 6.dp, bottomStart = 20.dp))
                    .background(AiChatPalette.accentGradient)
                    .padding(horizontal = 15.dp, vertical = 12.dp),
        ) {
            DsText(
                text = message.text,
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = DesignSystemThemeImpl.designSystemColor.contentOnAccent,
                maxLines = Int.MAX_VALUE,
            )
        }
    }
}

@Composable
private fun AiAvatar() {
    Box(
        modifier = Modifier.size(AvatarSize).clip(CircleShape).background(AiChatPalette.accentGradient),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_sparkle_24),
            contentDescription = null,
            tint = DesignSystemThemeImpl.designSystemColor.contentOnAccent,
            modifier = Modifier.size(20.dp),
        )
    }
}

// ---------------- quick reply pills ----------------

@Composable
private fun QuickReplyPills(
    replies: List<String>,
    kind: QuickReplyKind,
    enabled: Boolean,
    onQuickReply: (String) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(start = AvatarSize + 11.dp, top = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        replies.forEach { reply ->
            Row(
                modifier =
                    Modifier
                        .glass(CircleShape, elevation = 5.dp)
                        .clickable(enabled = enabled) { if (enabled) onQuickReply(reply) }
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Icon(
                    painter = painterResource(kind.iconRes()),
                    contentDescription = null,
                    tint = DesignSystemThemeImpl.designSystemColor.contentAccent,
                    modifier = Modifier.size(13.dp),
                )
                DsText(
                    text = reply,
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = AiChatPalette.ink,
                )
            }
        }
    }
}

@DrawableRes
private fun QuickReplyKind.iconRes(): Int =
    when (this) {
        QuickReplyKind.REGION -> R.drawable.ic_location_24
        QuickReplyKind.THEME -> R.drawable.ic_sparkle_24
        QuickReplyKind.PLACE -> R.drawable.ic_star_24
        QuickReplyKind.GENERIC -> R.drawable.ic_sparkle_24
    }

// ---------------- recommendation layouts ----------------

/**
 * 추천 3개 묶음. 배치는 [layout] 으로 고르며, 지금은 [AiRecLayout.FEATURED] 하나로 고정해 보여 준다.
 * 나머지 배치(CAROUSEL·STACKED)도 아래 when 분기로 살려 둔다 — 기본값만 바꾸면 그대로 쓸 수 있다.
 */
@Composable
private fun RecommendationBlock(
    recommendations: List<AiCourseRecommendation>,
    layout: AiRecLayout,
) {
    when (layout) {
        AiRecLayout.CAROUSEL -> CarouselLayout(recommendations)
        AiRecLayout.FEATURED -> FeaturedLayout(recommendations)
        AiRecLayout.STACKED -> StackedLayout(recommendations)
    }
}

/** A — 가로 스와이프 카드 + 점 인디케이터. */
@Composable
private fun CarouselLayout(recommendations: List<AiCourseRecommendation>) {
    val rowState = rememberLazyListState()
    val activeIndex by remember { derivedStateOf { rowState.firstVisibleItemIndex } }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        LazyRow(
            state = rowState,
            modifier = Modifier.widthIn(max = BubbleMaxWidth),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            itemsIndexed(recommendations) { index, rec ->
                Column(modifier = Modifier.width(212.dp).glass(CardRadius).padding(bottom = 2.dp)) {
                    CourseThumb(index = index, tag = rec.tagLabel, modifier = Modifier.fillMaxWidth().height(132.dp))
                    Column(modifier = Modifier.padding(13.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        DsText(
                            text = rec.title,
                            style = DesignSystemThemeImpl.typeScale.textStrongS,
                            color = AiChatPalette.ink,
                            maxLines = 2,
                        )
                        DsText(
                            rec.meta,
                            style = DesignSystemThemeImpl.typeScale.textRegularXS,
                            color = AiChatPalette.inkSoft,
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.widthIn(max = BubbleMaxWidth).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        ) {
            recommendations.indices.forEach { i ->
                val on = i == activeIndex
                Box(
                    modifier =
                        Modifier
                            .height(7.dp)
                            .width(if (on) 18.dp else 7.dp)
                            .clip(CircleShape)
                            .background(
                                if (on) {
                                    AiChatPalette.accentGradient
                                } else {
                                    Brush.linearGradient(
                                        listOf(
                                            AiChatPalette.inkSoft.copy(alpha = 0.35f),
                                            AiChatPalette.inkSoft.copy(alpha = 0.35f),
                                        ),
                                    )
                                },
                            ),
                )
            }
        }
    }
}

/** B — 대표 1개 크게 + 나머지 2개 작게. */
@Composable
private fun FeaturedLayout(recommendations: List<AiCourseRecommendation>) {
    val top = recommendations.first()
    Column(modifier = Modifier.widthIn(max = BubbleMaxWidth), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(modifier = Modifier.glass(CardRadius)) {
            Box {
                CourseThumb(index = 0, tag = null, modifier = Modifier.fillMaxWidth().height(138.dp))
                Box(
                    modifier =
                        Modifier
                            .padding(10.dp)
                            .clip(CircleShape)
                            .background(AiChatPalette.accentGradient)
                            .padding(horizontal = 11.dp, vertical = 5.dp),
                ) {
                    DsText(
                        "추천 1순위",
                        style = DesignSystemThemeImpl.typeScale.textStrongS,
                        color = DesignSystemThemeImpl.designSystemColor.contentOnAccent,
                    )
                }
            }
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                DsText(
                    top.title,
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = AiChatPalette.ink,
                    maxLines = 2,
                )
                DsText(top.meta, style = DesignSystemThemeImpl.typeScale.textRegularXS, color = AiChatPalette.inkSoft)
            }
        }
        recommendations.drop(1).forEachIndexed { i, rec ->
            MiniRecRow(index = i + 1, rec = rec)
        }
    }
}

@Composable
private fun MiniRecRow(
    index: Int,
    rec: AiCourseRecommendation,
) {
    Row(
        modifier = Modifier.fillMaxWidth().glass(RoundedCornerShape(18.dp)).padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        CourseThumb(index = index, tag = null, modifier = Modifier.size(width = 72.dp, height = 60.dp))
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            DsText(
                rec.title,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = AiChatPalette.ink,
                maxLines = 2,
            )
            DsText(rec.meta, style = DesignSystemThemeImpl.typeScale.textRegularXS, color = AiChatPalette.inkSoft)
        }
    }
}

/** C — 이미지-탑 세로 카드 3개. */
@Composable
private fun StackedLayout(recommendations: List<AiCourseRecommendation>) {
    Column(modifier = Modifier.widthIn(max = BubbleMaxWidth), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        recommendations.forEachIndexed { index, rec ->
            Column(modifier = Modifier.fillMaxWidth().glass(CardRadius)) {
                CourseThumb(index = index, tag = rec.tagLabel, modifier = Modifier.fillMaxWidth().height(108.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        DsText(
                            rec.title,
                            style = DesignSystemThemeImpl.typeScale.textStrongS,
                            color = AiChatPalette.ink,
                            maxLines = 1,
                        )
                        DsText(
                            rec.meta,
                            style = DesignSystemThemeImpl.typeScale.textRegularXS,
                            color = AiChatPalette.inkSoft,
                        )
                    }
                }
            }
        }
    }
}

/** 코스 썸네일 자리 — 파스텔 그라데이션 + (옵션) 좌상단 태그. 목업이라 실제 이미지는 없다. */
@Composable
private fun CourseThumb(
    index: Int,
    tag: String?,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.clip(RoundedCornerShape(14.dp)).background(AiChatPalette.thumbBrush(index))) {
        if (tag != null) {
            Box(
                modifier =
                    Modifier
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(AiChatPalette.glassTop)
                        .padding(horizontal = 9.dp, vertical = 3.dp),
            ) {
                DsText(tag, style = DesignSystemThemeImpl.typeScale.textRegularXS, color = AiChatPalette.ink)
            }
        }
    }
}

// ---------------- typing + input ----------------

@Composable
private fun TypingRow() {
    Row(verticalAlignment = Alignment.Top) {
        AiAvatar()
        Spacer(Modifier.width(11.dp))
        Box(
            modifier =
                Modifier
                    .glass(RoundedCornerShape(topStart = 6.dp, topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 20.dp))
                    .padding(horizontal = 18.dp, vertical = 15.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                repeat(3) { index -> TypingDot(index) }
            }
        }
    }
}

@Composable
private fun TypingDot(index: Int) {
    val transition = rememberInfiniteTransition(label = "typing")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600, delayMillis = index * 150), RepeatMode.Reverse),
        label = "dot$index",
    )
    Box(
        modifier =
            Modifier
                .size(6.dp)
                .alpha(alpha)
                .clip(CircleShape)
                .background(AiChatPalette.inkSoft),
    )
}

@Composable
private fun ChatInputBar(
    enabled: Boolean,
    onSend: (String) -> Unit,
) {
    var input by remember { mutableStateOf("") }
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = ScreenPadding, vertical = 10.dp),
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

@Composable
private fun SendButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(44.dp)
                .clip(CircleShape)
                .then(
                    if (enabled) {
                        Modifier.background(AiChatPalette.accentGradient)
                    } else {
                        Modifier.background(DesignSystemThemeImpl.designSystemColor.bgAccentSubtle)
                    },
                ).clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_up_24),
            contentDescription = "보내기",
            tint =
                if (enabled) {
                    DesignSystemThemeImpl.designSystemColor.contentOnAccent
                } else {
                    DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2
                },
            modifier = Modifier.size(22.dp),
        )
    }
}

// ---------------- glass + aurora helpers ----------------

/** 반투명 글래스 표면(흰 그라데이션 + 테두리 + 그림자). */
private fun Modifier.glass(
    shape: Shape,
    elevation: androidx.compose.ui.unit.Dp = 12.dp,
): Modifier =
    this
        .shadow(
            elevation,
            shape,
            spotColor = AiChatPalette.ink.copy(alpha = 0.25f),
            ambientColor = AiChatPalette.ink.copy(alpha = 0.2f),
        ).clip(shape)
        .background(AiChatPalette.glassSurface)
        .border(1.dp, AiChatPalette.glassBorder, shape)

/** 오로라 블롭을 아주 느리게 위아래로 흘린다(reduce-motion 시 정지는 Compose 가 알아서 하지 않으므로 폭만 작게). */
@Composable
private fun rememberAuroraDrift(): Float {
    val transition = rememberInfiniteTransition(label = "aurora")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(20000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "drift",
    )
    return drift
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAurora(drift: Float) {
    drawRect(AiChatPalette.auroraBase)
    val shift = (drift - 0.5f) * 32.dp.toPx()

    fun blob(
        color: androidx.compose.ui.graphics.Color,
        fx: Float,
        fy: Float,
        fr: Float,
    ) {
        val center = Offset(size.width * fx, size.height * fy + shift)
        drawRect(
            brush =
                Brush.radialGradient(
                    colors = listOf(color.copy(alpha = 0.85f), color.copy(alpha = 0f)),
                    center = center,
                    radius = size.minDimension * fr,
                ),
        )
    }
    blob(AiChatPalette.blobMint, 0.18f, 0.16f, 0.60f)
    blob(AiChatPalette.blobSky, 0.90f, 0.10f, 0.62f)
    blob(AiChatPalette.blobTeal, 0.82f, 0.86f, 0.66f)
    blob(AiChatPalette.blobLavender, 0.10f, 0.92f, 0.58f)
}

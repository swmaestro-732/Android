package com.chillsam.courmy.main.presentation.aichat

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.DsTextField
import com.chillsam.courmy.common.presentation.helper.LocalSessionUiState
import com.chillsam.courmy.common.presentation.helper.StatusBarColor
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.aichat.AiChatStep
import com.chillsam.courmy.main.entity.aichat.QuickReplyVO
import com.chillsam.courmy.main.entity.home.HomeCourseVO
import com.chillsam.courmy.main.presentation.component.hasLocationPermission
import com.google.android.gms.location.LocationServices
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val ScreenPadding = 16.dp
private val BubbleMaxWidth = 272.dp
private val AvatarSize = 40.dp

/**
 * 커미 AI 코스 추천 채팅 화면(파스텔 + 글래스). 서버 주도 대화([AiCourseChatViewModel]).
 *
 * 로그인 전용이라 비로그인은 로그인 안내로 막는다. 진입 시 위치 권한을 확인해 좌표를 ViewModel 에 전달하고
 * (지역 칩·좌표가 필요한 서버 칩), 서버 턴의 말풍선·칩·입력창·코스를 그대로 그린다. 코스 카드를 누르면 상세로 간다.
 */
@Composable
fun AiCourseChatPage(
    onBack: () -> Unit,
    onLogin: () -> Unit,
    onCourseClick: (String) -> Unit,
    viewModel: AiCourseChatViewModel = hiltViewModel(),
) {
    val session = LocalSessionUiState.current
    if (!session.isLoggedIn) {
        LoginGate(onBack = onBack, onLogin = onLogin)
        return
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.onIntent(AiCourseChatIntent.Start) }
    LocationEffect { lat, lng -> viewModel.onIntent(AiCourseChatIntent.ProvideLocation(lat, lng)) }

    AiCourseChatScreen(
        state = state,
        onBack = onBack,
        onSend = { viewModel.onIntent(AiCourseChatIntent.SendText(it)) },
        onRegionChip = { viewModel.onIntent(AiCourseChatIntent.RegionChipTap(it)) },
        onQuickReply = { viewModel.onIntent(AiCourseChatIntent.QuickReplyTap(it)) },
        onCourseClick = onCourseClick,
    )
}

/** 위치 권한을 확인·요청하고, 있으면 마지막 좌표를 [onLocation] 으로 넘긴다(지역 칩·좌표가 필요한 서버 칩용). */
@Composable
private fun LocationEffect(onLocation: (Double, Double) -> Unit) {
    val context = LocalContext.current
    var tick by remember { mutableIntStateOf(0) }
    val hasPermission = remember(tick) { context.hasLocationPermission() }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { tick++ }
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            fetchLastLocation(context, onLocation)
        } else {
            launcher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            )
        }
    }
}

@SuppressLint("MissingPermission")
private fun fetchLastLocation(
    context: Context,
    onLocation: (Double, Double) -> Unit,
) {
    LocationServices
        .getFusedLocationProviderClient(context)
        .lastLocation
        .addOnSuccessListener { location -> location?.let { onLocation(it.latitude, it.longitude) } }
}

@Composable
private fun AiCourseChatScreen(
    state: AiCourseChatUIState,
    onBack: () -> Unit,
    onSend: (String) -> Unit,
    onRegionChip: (String) -> Unit,
    onQuickReply: (QuickReplyVO) -> Unit,
    onCourseClick: (String) -> Unit,
) {
    StatusBarColor(AiChatPalette.auroraBase.copy(alpha = 0f))
    val drift = rememberAuroraDrift()
    val showRegion = state.showRegionChips && state.regionChips.isNotEmpty() && !state.isAiTyping
    val showServer = !state.showRegionChips && state.quickReplies.isNotEmpty() && !state.isAiTyping
    val hasChips = showRegion || showServer

    Box(modifier = Modifier.fillMaxSize().drawBehind { drawAurora(drift) }) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            ChatTopBar(onBack = onBack)

            val listState = rememberLazyListState()
            LaunchedEffect(state.messages.size, hasChips, state.isAiTyping) {
                val extra = (if (hasChips) 1 else 0) + (if (state.isAiTyping) 1 else 0)
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
                    MessageRow(message = message, onCourseClick = onCourseClick)
                }
                if (showRegion) {
                    item(key = "regionChips") {
                        ChipRow(state.regionChips.map { it to true }, !state.isAiTyping) { onRegionChip(it) }
                    }
                } else if (showServer) {
                    item(key = "serverChips") {
                        ServerChipRow(state.quickReplies, !state.isAiTyping, onQuickReply)
                    }
                }
                if (state.isAiTyping) {
                    item(key = "typing") { TypingRow() }
                }
            }

            if (state.step == AiChatStep.RESULT) {
                Spacer(Modifier.navigationBarsPadding())
            } else {
                ChatInputBar(
                    enabled = state.inputEnabled,
                    placeholder = state.inputPlaceholder,
                    onSend = onSend,
                )
            }
        }
    }
}

// ---------------- login gate ----------------

@Composable
private fun LoginGate(
    onBack: () -> Unit,
    onLogin: () -> Unit,
) {
    StatusBarColor(AiChatPalette.auroraBase.copy(alpha = 0f))
    val drift = rememberAuroraDrift()
    Box(modifier = Modifier.fillMaxSize().drawBehind { drawAurora(drift) }) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            ChatTopBar(onBack = onBack)
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AiAvatar()
                Spacer(Modifier.height(16.dp))
                DsText(
                    text = "로그인하고\n커미 AI에게 코스를 추천받아 보세요",
                    style = DesignSystemThemeImpl.typeScale.textStrongM,
                    color = AiChatPalette.ink,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(24.dp))
                DsButton(text = "로그인하기", onClick = onLogin, modifier = Modifier.widthIn(max = 240.dp))
            }
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
        DsText("커미 AI", style = DesignSystemThemeImpl.typeScale.textStrongS, color = AiChatPalette.ink)
        DsText("Beta", style = DesignSystemThemeImpl.typeScale.textRegularXS, color = AiChatPalette.inkSoft)
    }
}

// ---------------- message rows ----------------

@Composable
private fun MessageRow(
    message: AiChatMessage,
    onCourseClick: (String) -> Unit,
) {
    when (message.role) {
        ChatRole.AI -> AiMessageRow(message = message, onCourseClick = onCourseClick)
        ChatRole.USER -> UserMessageRow(message)
    }
}

@Composable
private fun AiMessageRow(
    message: AiChatMessage,
    onCourseClick: (String) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        AiAvatar()
        Spacer(Modifier.width(11.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (message.text.isNotBlank()) GlassBubble(message.text)
            if (message.courses.isNotEmpty()) {
                Column(
                    modifier = Modifier.widthIn(max = BubbleMaxWidth),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    message.courses.forEach { course ->
                        ChatCourseCard(course = course, onClick = { onCourseClick(course.id) })
                    }
                }
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
private fun AiAvatar(isResponding: Boolean = false) {
    val surface =
        if (isResponding) {
            val transition = rememberInfiniteTransition(label = "avatarSurface")
            val phase by transition.animateFloat(
                initialValue = 0f,
                targetValue = (2 * PI).toFloat(),
                animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing)),
                label = "waterSurface",
            )
            Modifier.drawBehind {
                val radius = size.minDimension * 0.45f
                val path = Path()
                repeat(65) { index ->
                    val angle = (2 * PI * index / 64).toFloat()
                    val ripple = 1f + 0.08f * sin(2 * angle + phase) + 0.04f * sin(3 * angle - phase)
                    val x = center.x + radius * ripple * cos(angle)
                    val y = center.y + radius * ripple * sin(angle)
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                path.close()
                drawPath(path, AiChatPalette.accentGradient)
            }
        } else {
            Modifier.background(AiChatPalette.accentGradient, CircleShape)
        }
    Box(
        modifier = Modifier.size(AvatarSize).then(surface),
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

// ---------------- chips ----------------

/** 지역 칩(앱 소유). 라벨만 받아 그린다 — 눌리면 [onTap] 에 라벨을 넘긴다. */
@Composable
private fun ChipRow(
    chips: List<Pair<String, Boolean>>,
    enabled: Boolean,
    onTap: (String) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(start = AvatarSize + 11.dp, top = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        chips.forEach { (label, withPin) ->
            ChipPill(label = label, iconRes = if (withPin) R.drawable.ic_location_24 else null, enabled = enabled) {
                onTap(label)
            }
        }
    }
}

/** 서버 칩(THEME 이후). value 는 해석하지 않고 그대로 되돌려 보낸다. */
@Composable
private fun ServerChipRow(
    replies: List<QuickReplyVO>,
    enabled: Boolean,
    onTap: (QuickReplyVO) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(start = AvatarSize + 11.dp, top = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        replies.forEach { reply ->
            val icon = if (reply.requiresLocation) R.drawable.ic_location_24 else R.drawable.ic_sparkle_24
            ChipPill(label = reply.label, iconRes = icon, enabled = enabled) { onTap(reply) }
        }
    }
}

@Composable
private fun ChipPill(
    label: String,
    iconRes: Int?,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .glass(CircleShape, elevation = 5.dp)
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        if (iconRes != null) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = DesignSystemThemeImpl.designSystemColor.contentAccent,
                modifier = Modifier.size(13.dp),
            )
        }
        DsText(text = label, style = DesignSystemThemeImpl.typeScale.textRegularS, color = AiChatPalette.ink)
    }
}

// ---------------- course card ----------------

/** 작은 썸네일·테마·제목의 가로형 카드. 카드 전체를 누르면 코스 상세로 이동한다. */
@Composable
private fun ChatCourseCard(
    course: HomeCourseVO,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(AiChatPalette.glassTop)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(10.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DesignSystemThemeImpl.designSystemColor.imagePlaceholder),
        ) {
            if (course.coverImageUrl.isNotBlank()) {
                AsyncImage(
                    model = course.coverImageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (course.categoryLabel.isNotBlank()) {
                DsText(
                    course.categoryLabel,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = DesignSystemThemeImpl.designSystemColor.contentAccent,
                    maxLines = 1,
                )
            }
            DsText(
                course.title,
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = AiChatPalette.ink,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

// ---------------- typing + input ----------------

@Composable
private fun TypingRow() {
    Row(verticalAlignment = Alignment.Top) {
        AiAvatar(isResponding = true)
        Spacer(Modifier.width(11.dp))
        Box(
            modifier =
                Modifier
                    .glass(RoundedCornerShape(topStart = 6.dp, topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 20.dp))
                    .padding(horizontal = 18.dp, vertical = 15.dp),
        ) {
            Row(
                modifier = Modifier.height(14.dp).semantics { contentDescription = "응답 준비 중" },
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(3) { index -> TypingDot(index) }
            }
        }
    }
}

@Composable
private fun TypingDot(index: Int) {
    val transition = rememberInfiniteTransition(label = "typing")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    keyframes {
                        durationMillis = 900
                        0f at 0 using FastOutSlowInEasing
                        -4f at 250 using FastOutSlowInEasing
                        0f at 500
                    },
                initialStartOffset = StartOffset(index * 150),
            ),
        label = "dot$index",
    )
    Box(
        modifier =
            Modifier
                .size(6.dp)
                .graphicsLayer { translationY = offset.dp.toPx() }
                .clip(CircleShape)
                .background(AiChatPalette.inkSoft),
    )
}

@Composable
private fun ChatInputBar(
    enabled: Boolean,
    placeholder: String,
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
            placeholder = placeholder,
            enabled = enabled,
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

package com.chillsam.courmy.main.presentation.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsDialogScaffold
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.StatusBarColor
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

/**
 * 스플래시 화면(FS-01). 초록 배경 위에 "my course → Courmy" 로고 모프 애니메이션을 재생하고,
 * 재생이 끝나면 온보딩 완료 여부·자동 로그인 여부에 따라 [onFinished] 로 다음 화면을 알린다.
 */
@Composable
fun SplashPage(
    onFinished: (onboarded: Boolean, loggedIn: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    // 화면 전체가 브랜드 초록이라 상태바까지 잇는다(아이콘 명암은 휘도로 자동 결정된다).
    StatusBarColor(DesignSystemThemeImpl.designSystemColor.bgAccent)
    val color = DesignSystemThemeImpl.designSystemColor
    // 로고 크기를 폰트에 반영(런타임 스케일 없이 크게 → 스케일 레이어/히치 방지).
    val logoStyle =
        DesignSystemThemeImpl.typeScale.displayExtraXL.let {
            it.copy(
                fontSize = (it.fontSize.value * 1.34f).sp,
                lineHeight = (it.fontSize.value * 1.7f).sp,
            )
        }
    val progress = remember { Animatable(0f) }
    // 종료 페이즈: 초록 배경을 온보딩 흰 배경으로 수렴 + 로고 페이드아웃(0→1).
    val exit = remember { Animatable(0f) }

    LaunchedEffectMorph(progress, exit, viewModel, onFinished)

    // 오프라인이면 스플래시에 머문다. 목적지가 비어 있어 위 모프가 마지막 단계에서 멈춰 있고,
    // 다이얼로그는 닫히지 않으므로 다시 연결될 때까지 앱으로 들어갈 수 없다.
    val isOffline by viewModel.isOffline.collectAsStateWithLifecycle()
    if (isOffline) {
        OfflineBlockingDialog(onRetry = viewModel::retry)
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .drawWithCache {
                    val background =
                        Brush.radialGradient(
                            colors = listOf(color.bgAccent, color.bgAccentPressed),
                            center = Offset(size.width / 2f, size.height * 0.38f),
                            radius = size.maxDimension * 0.82f,
                        )
                    val whiten = color.bgDefaultLevel1
                    onDrawBehind {
                        drawRect(background)
                        // 종료 시 온보딩 흰 배경으로 수렴(흰→흰으로 이어져 색 점프 없음).
                        val e = exit.value
                        if (e > 0f) drawRect(whiten, alpha = e)
                    }
                },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .offset(y = (-40).dp)
                    .graphicsLayer { alpha = 1f - exit.value },
            contentAlignment = Alignment.Center,
        ) {
            // 애니메이션 값은 graphicsLayer 람다(그리기 단계)에서만 읽어 프레임마다 recomposition 하지 않는다.
            Box(
                modifier =
                    Modifier
                        .size(150.dp)
                        .graphicsLayer {
                            val p = progress.value
                            val ringProgress = (p / 0.72f).coerceIn(0f, 1f)
                            alpha = minOf((p / 0.1f).coerceIn(0f, 1f), 1f - ringProgress) * 0.42f
                            val s = lerp(0.72f, 1.9f, FastOutSlowInEasing.transform(ringProgress))
                            scaleX = s
                            scaleY = s
                        }.border(1.5.dp, color.contentOnAccent, CircleShape),
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                DsText(
                    text = "my course",
                    style = DesignSystemThemeImpl.typeScale.displayExtraXL,
                    color = color.contentOnAccent,
                    modifier =
                        Modifier.graphicsLayer {
                            val p = progress.value
                            alpha =
                                minOf(
                                    (p / 0.1f).coerceIn(0f, 1f),
                                    (1f - (p - 0.26f) / 0.14f).coerceIn(0f, 1f),
                                )
                        },
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                        Modifier
                            .offset(y = (-38).dp)
                            .graphicsLayer {
                                // 큰 로고는 스케일 없이 페이드만(스케일 애니메이션이 프레임을 끊음).
                                // 아주 작은 alpha 를 유지해 첫 등장 프레임의 글리프 래스터화 히치를 예열한다.
                                alpha = ((progress.value - 0.36f) / 0.16f).coerceIn(0f, 1f).coerceAtLeast(0.02f)
                            },
                ) {
                    DsText(
                        text = "Courmy",
                        style = logoStyle,
                        color = color.contentOnAccent,
                    )
                    DsText(
                        text = ".",
                        style = logoStyle,
                        color = color.contentOnAccent.copy(alpha = 0.72f),
                        modifier =
                            Modifier.graphicsLayer {
                                val p = progress.value
                                alpha = ((p - 0.54f) / 0.1f).coerceIn(0f, 1f)
                                val d =
                                    lerp(
                                        0.35f,
                                        1f,
                                        FastOutSlowInEasing.transform(((p - 0.54f) / 0.16f).coerceIn(0f, 1f)),
                                    )
                                scaleX = d
                                scaleY = d
                            },
                    )
                }
                DsText(
                    text = "나만의 코스, 나만의 동네",
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentOnAccent.copy(alpha = 0.82f),
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier
                            .offset(y = (-22).dp)
                            .graphicsLayer { alpha = ((progress.value - 0.6f) / 0.14f).coerceIn(0f, 1f) },
                )
            }
        }
    }
}

/**
 * 네트워크가 없을 때 앱 진입을 막는 안내.
 *
 * 닫을 방법을 두지 않는다(스크림·뒤로가기 모두 무시) — 오프라인에서 들여보내면 빈 화면과 실패
 * 토스트만 보이므로, 연결을 되찾는 것 말고 할 수 있는 일이 없다.
 */
@Composable
private fun OfflineBlockingDialog(onRetry: () -> Unit) {
    DsDialogScaffold(
        title = "인터넷 연결이 필요해요",
        description = "Wi-Fi 나 데이터가 켜져 있는지 확인하고 다시 시도해 주세요.",
        onDismiss = {},
    ) {
        DsButton(text = "다시 시도", onClick = onRetry)
    }
}

/** 모프 애니메이션을 재생하고, 완료 + 세션/온보딩 로드 후 [onFinished] 호출. */
@Composable
private fun LaunchedEffectMorph(
    progress: Animatable<Float, *>,
    exit: Animatable<Float, *>,
    viewModel: SplashViewModel,
    onFinished: (Boolean, Boolean) -> Unit,
) {
    androidx.compose.runtime.LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(durationMillis = 3200, easing = FastOutSlowInEasing))
        delay(350)
        val destination = viewModel.destination.filterNotNull().first()
        // 초록 배경을 흰색으로 수렴시키며 로고를 함께 페이드아웃한 뒤 다음 화면으로.
        exit.animateTo(1f, tween(durationMillis = 480, easing = FastOutSlowInEasing))
        onFinished(destination.onboarded, destination.loggedIn)
    }
}

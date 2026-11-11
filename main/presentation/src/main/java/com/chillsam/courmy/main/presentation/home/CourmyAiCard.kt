package com.chillsam.courmy.main.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.presentation.aichat.AiChatPalette

private val CardShape = RoundedCornerShape(22.dp)

/**
 * 홈(FS-09)의 "커미 AI" 코스 추천 배너.
 *
 * 눌러서 커미 AI 채팅([AiCourseChatPage])으로 진입한다. 채팅 화면과 결을 맞춰
 * 몽환적 민트·초록 파스텔 오로라 배경 + 글래스 요소로 꾸민다. 아바타·CTA 는 채팅과 같은 초록 그라데이션.
 * 게스트에서도 노출하는 둘러보기 진입점이라 세션 분기 없이 보인다(노출 여부는 FeatureFlags 가 관리).
 */
@Composable
fun CourmyAiCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = CardShape,
                    spotColor = AiChatPalette.ink.copy(alpha = 0.20f),
                    ambientColor = AiChatPalette.ink.copy(alpha = 0.08f),
                ).clip(CardShape)
                .drawBehind { drawCardAurora() }
                .clickable(onClick = onClick)
                .padding(20.dp),
    ) {
        // 우하단 장식 글래스 원 — 카드 clip 으로 바깥이 잘려 반원처럼 떠 보인다.
        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 26.dp, y = 26.dp)
                    .size(132.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.28f)),
        )

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // 커미 AI 정체성: 채팅과 같은 초록 그라데이션 아바타 + 라벨.
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(AiChatPalette.accentGradient),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_sparkle_24),
                        contentDescription = null,
                        tint = DesignSystemThemeImpl.designSystemColor.contentOnAccent,
                        modifier = Modifier.size(17.dp),
                    )
                }
                DsText(
                    text = "커미 AI",
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = AiChatPalette.ink,
                )
            }

            DsText(
                text = "취향만 알려주면\n코스를 뚝딱 짜줘요",
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = AiChatPalette.ink,
                maxLines = 2,
            )

            // 우측 텍스트 링크. 클릭 영역은 배너 전체를 유지한다.
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                DsText(
                    text = "코스 추천받기",
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = DesignSystemThemeImpl.designSystemColor.contentAccent,
                )
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_up_24),
                    contentDescription = null,
                    tint = DesignSystemThemeImpl.designSystemColor.contentAccent,
                    modifier = Modifier.size(20.dp).rotate(45f),
                )
            }
        }
    }
}

/** 카드 배경에 채팅과 같은 민트·초록·라벤더 오로라를 정적으로 깐다(리스트 아이템이라 애니메이션 없음). */
private fun DrawScope.drawCardAurora() {
    drawRect(AiChatPalette.cardBase)

    fun blob(
        color: Color,
        fx: Float,
        fy: Float,
        fr: Float,
    ) {
        drawRect(
            brush =
                Brush.radialGradient(
                    colors = listOf(color.copy(alpha = 0.9f), color.copy(alpha = 0f)),
                    center = Offset(size.width * fx, size.height * fy),
                    radius = size.minDimension * fr,
                ),
        )
    }
    blob(AiChatPalette.blobMint, 0.08f, 0.1f, 1.1f)
    blob(AiChatPalette.blobSky, 1.0f, 0.0f, 1.0f)
    blob(AiChatPalette.blobLavender, 0.9f, 1.2f, 1.0f)
}

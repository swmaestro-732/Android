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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

private val CardShape = RoundedCornerShape(20.dp)
private const val PILL_BG_ALPHA = 0.16f
private const val DECOR_CIRCLE_ALPHA = 0.08f

/**
 * 홈(FS-09)의 "커미 AI" 코스 추천 배너.
 *
 * 짙은 초록 그라데이션 카드로, 눌러서 커미 AI 채팅([AiCourseChatPage])으로 진입한다.
 * 게스트에서도 노출하는 둘러보기 진입점이라 세션 분기 없이 항상 보인다.
 * 색은 모두 시맨틱 토큰(bgAccent/bgAccentPressed/contentOnAccent)으로 구성한다.
 */
@Composable
fun CourmyAiCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(CardShape)
                .background(
                    // 좌상단이 짙고 우하단이 밝은 대각 그라데이션(목업 기준).
                    Brush.linearGradient(
                        colors = listOf(color.bgAccentPressed, color.bgAccent),
                    ),
                ).clickable(onClick = onClick)
                .padding(20.dp),
    ) {
        // 우하단 장식 원 — 카드 clip 으로 바깥이 잘려 반원처럼 보인다.
        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 24.dp, y = 24.dp)
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(color.contentOnAccent.copy(alpha = DECOR_CIRCLE_ALPHA)),
        )

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // "✨ 커미 AI" 라벨 알약
            Row(
                modifier =
                    Modifier
                        .clip(CircleShape)
                        .background(color.contentOnAccent.copy(alpha = PILL_BG_ALPHA))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_sparkle_24),
                    contentDescription = null,
                    tint = color.contentOnAccent,
                    modifier = Modifier.size(14.dp),
                )
                DsText(
                    text = "커미 AI",
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentOnAccent,
                )
            }

            DsText(
                text = "취향만 알려주면\n코스를 뚝딱 짜줘요",
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentOnAccent,
                maxLines = 2,
            )

            // 흰 알약 버튼: "코스 추천받기 →"
            Row(
                modifier =
                    Modifier
                        .clip(CircleShape)
                        .background(color.contentOnAccent)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                DsText(
                    text = "코스 추천받기",
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = color.contentDefaultLevel0,
                )
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_right_24),
                    contentDescription = null,
                    tint = color.contentDefaultLevel0,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

package com.chillsam.courmy.main.presentation.aichat

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * 커미 AI 채팅 화면 **전용 장식 색**.
 *
 * 몽환적 파스텔 오로라 배경·글래스 카드·코스 썸네일 그라데이션처럼 이 화면에서만 쓰는 꾸밈 색이다.
 * 공용 디자인 토큰(시맨틱 슬롯/팔레트)에 넣으면 Figma 유사색과 중복돼 섞이므로, 재사용 의도가 없는
 * 장식 색은 여기 feature-로컬로 모아 둔다([[design-token-slot-policy]] 취지). 초록 브랜드 포인트는
 * 토큰(`DesignSystemThemeImpl.designSystemColor.bgAccent` 등)을 그대로 쓴다.
 *
 * 이 기능이 프리뷰(FeatureFlag)를 벗어나 정식화되면 디자인 시스템과 함께 정리한다. [wiki-needed]
 */
internal object AiChatPalette {
    /** 오로라 배경의 베이스(거의 흰색, 아주 옅은 민트). */
    val auroraBase = Color(0xFFF4FAF6)

    /** 오로라 블롭 4색 — 민트·하늘·틸·라벤더. 알파로 번지게 깐다. */
    val blobMint = Color(0xFFA9E9C9)
    val blobSky = Color(0xFFBFE0F5)
    val blobTeal = Color(0xFFBDEBD6)
    val blobLavender = Color(0xFFD8D3F3)

    /** 글래스 표면/테두리(흰색 반투명). */
    val glassTop = Color(0xFFFFFFFF).copy(alpha = 0.74f)
    val glassBottom = Color(0xFFFFFFFF).copy(alpha = 0.44f)
    val glassBorder = Color(0xFFFFFFFF).copy(alpha = 0.72f)

    /** 본문/보조 텍스트(초록빛 짙은 슬레이트). 흰 글래스 위에서 토큰 본문색보다 이 화면 톤에 맞는다. */
    val ink = Color(0xFF1D2A23)
    val inkSoft = Color(0xFF6A7A71)

    /** 코스 썸네일 그라데이션 3종(인덱스로 순환). */
    private val thumbColors =
        listOf(
            Color(0xFF9EDDBE) to Color(0xFF73C7E6),
            Color(0xFFB7E6C9) to Color(0xFF8ED2B4),
            Color(0xFFA9CFF0) to Color(0xFFC7BCEF),
        )

    fun thumbBrush(index: Int): Brush {
        val (start, end) = thumbColors[index % thumbColors.size]
        return Brush.linearGradient(listOf(start, end))
    }

    val glassSurface: Brush
        get() = Brush.verticalGradient(listOf(glassTop, glassBottom))

    /**
     * 초록 포인트 그라데이션(아바타·보내기·유저 말풍선). 값은 브랜드 팔레트 Forest500→Forest600 과 같다.
     * 시맨틱 슬롯(success 등)을 꾸밈용으로 돌려쓰지 않으려 이 화면에서만 로컬로 참조한다.
     */
    private val accentLight = Color(0xFF498056)
    private val accentDark = Color(0xFF30623C)

    val accentGradient: Brush
        get() = Brush.linearGradient(listOf(accentLight, accentDark))
}

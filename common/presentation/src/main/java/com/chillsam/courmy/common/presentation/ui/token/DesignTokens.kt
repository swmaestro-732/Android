package com.chillsam.courmy.common.presentation.ui.token

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.chillsam.courmy.common.presentation.ui.color.DesignSystemSemanticColors
import com.chillsam.courmy.common.presentation.ui.typo.DesignSystemTypeScale
import com.chillsam.courmy.common.presentation.ui.typo.DsStaticTypeScale
import com.chillsam.courmy.common.presentation.ui.typo.pretendardTextFont

// =====================================================================================
// 디자인 토큰 단일 정의 파일.
// Figma Variables / Text Styles 를 코드에 반영할 때는 이 파일만 수정한다.
// (슬롯 구조 자체를 바꿀 때만 DesignSystemSemanticColors / DesignSystemTypeScale 을 수정)
// 매핑 규칙: docs/architecture/design-system.md
// =====================================================================================

// FIGMA-TOKEN-INJECTION-POINT: palette
// Figma Variable `palette/{name}/{step}` → CourmyPaletteColors.{Name}{Step}
// 예: palette/gray/900 → Gray900, palette/blue/400 → Blue400
@Suppress("Unused")
internal enum class CourmyPaletteColors(
    val colorValue: Color,
) {
    Black(Color(0xFF1E1E1C)),
    Gray300(Color(0xFFF5F5F3)),
    Gray400(Color(0xFFEAEAE7)),
    Gray500(Color(0xFFABABA3)),
    Gray600(Color(0xFF9C9C95)),
    Gray700(Color(0xFF67675F)),
    Gray800(Color(0xFF545450)),
    Gray900(Color(0xFF3A3A38)),
    White(Color(0xFFFFFFFF)),
    Green(Color(0xFF3E8C70)),
    Yellow(Color(0xFFF5A524)),
    Blue(Color(0xFF3A82E6)),
    Red(Color(0xFFC0584E)),
    Forest50(Color(0xFFF0FAF1)),
    Forest100(Color(0xFFDDF2E0)),
    Forest200(Color(0xFFBCDFC2)),
    Forest300(Color(0xFF92C29B)),
    Forest400(Color(0xFF6CA177)),
    Forest500(Color(0xFF498056)),
    Forest600(Color(0xFF30623C)),
    Forest700(Color(0xFF1C4E2A)),
    ForestTint(Color(0xFFEBF3ED)),
}

// FIGMA-TOKEN-INJECTION-POINT: semantic-colors
// Figma Variable `{role}/{variant}/{level}` → {role}{Variant}{Level}
// 예: bg/default/level0 → bgDefaultLevel0, content/accent → contentAccent
// 시맨틱 슬롯은 반드시 위 팔레트를 참조한다 (raw hex 직접 사용 금지).
val DefaultDesignSystemColor =
    DesignSystemSemanticColors(
        bgDefaultLevel0 = CourmyPaletteColors.Gray300.colorValue,
        bgDefaultLevel1 = CourmyPaletteColors.White.colorValue,
        // 강조 배경(Primary CTA). content 계열과 값(Forest600)은 겹치지만 역할이 달라 슬롯 분리.
        bgAccent = CourmyPaletteColors.Forest600.colorValue,
        bgAccentPressed = CourmyPaletteColors.Forest700.colorValue,
        // Secondary 버튼 배경(옅은 강조). Figma #eaf0ea ≈ ForestTint 로 스냅.
        bgAccentSubtle = CourmyPaletteColors.ForestTint.colorValue,
        borderDefaultLevel0 = CourmyPaletteColors.Gray400.colorValue,
        // 입력 필드 focus/error 테두리. content 계열과 값은 겹치나 역할(테두리)이 달라 분리.
        borderAccent = CourmyPaletteColors.Forest600.colorValue,
        borderDanger = CourmyPaletteColors.Red.colorValue,
        contentDefaultLevel0 = CourmyPaletteColors.Black.colorValue,
        contentDefaultLevel1 = CourmyPaletteColors.Gray800.colorValue,
        contentDefaultLevel2 = CourmyPaletteColors.Gray600.colorValue,
        contentDefaultLevel3 = CourmyPaletteColors.Gray500.colorValue,
        contentOnAccent = CourmyPaletteColors.White.colorValue,
        contentAccent = CourmyPaletteColors.Forest600.colorValue,
        contentRating = CourmyPaletteColors.Yellow.colorValue,
        contentDanger = CourmyPaletteColors.Red.colorValue,
        contentSuccess = CourmyPaletteColors.Green.colorValue,
        contentLocation = CourmyPaletteColors.Blue.colorValue,
    )

// FIGMA-TOKEN-INJECTION-POINT: type-scale
// Figma Text Style `{group}/{weight}/{size}` → {group}{Weight}{Size}
// weight 등급: Regular(Medium·SemiBold) < Strong(Bold) < Extra(ExtraBold)
// lineHeight·letterSpacing 은 Figma Text Style 실측값. letterSpacing 은 em 단위(px÷size).
internal val DefaultDesignSystemStaticTypeScale =
    DesignSystemTypeScale(
        // Display · ExtraBold 34 — 큰 헤드라인
        _displayExtraXL =
            DsStaticTypeScale(
                fontFamily = pretendardTextFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 34,
                lineHeight = 37.4f,
                letterSpacing = -0.03f,
            ),
        // Title · ExtraBold 24 — 화면 타이틀
        _titleExtraL =
            DsStaticTypeScale(
                fontFamily = pretendardTextFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24,
                lineHeight = 31.2f,
                letterSpacing = 0f,
            ),
        // Heading · Bold 19 — 섹션 제목
        _textStrongM =
            DsStaticTypeScale(
                fontFamily = pretendardTextFont,
                fontWeight = FontWeight.Bold,
                fontSize = 19,
                lineHeight = 25.65f,
                letterSpacing = 0f,
            ),
        // (사용자 추가) Medium 19 — Heading 비강조 버전. Figma 원본 없음(잠정값)
        _textRegularM =
            DsStaticTypeScale(
                fontFamily = pretendardTextFont,
                fontWeight = FontWeight.Medium,
                fontSize = 19,
                lineHeight = 25.65f,
                letterSpacing = 0f,
            ),
        // Body · Medium 16 — 본문
        _textRegularS =
            DsStaticTypeScale(
                fontFamily = pretendardTextFont,
                fontWeight = FontWeight.Medium,
                fontSize = 16,
                lineHeight = 25.6f,
                letterSpacing = 0f,
            ),
        // Caption · SemiBold 13 — 보조·라벨
        _textRegularXS =
            DsStaticTypeScale(
                fontFamily = pretendardTextFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13,
                lineHeight = 18f,
                letterSpacing = 0f,
            ),
        // Overline · ExtraBold 13 — 대문자 라벨(문자열 자체를 대문자로 넣어 사용)
        _textExtraXS =
            DsStaticTypeScale(
                fontFamily = pretendardTextFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13,
                lineHeight = 16f,
                letterSpacing = 0.22f,
            ),
    )

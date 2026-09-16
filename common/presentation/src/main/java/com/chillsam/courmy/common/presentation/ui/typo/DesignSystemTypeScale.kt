package com.chillsam.courmy.common.presentation.ui.typo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.em
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

@ConsistentCopyVisibility
@Suppress("ConstructorParameterNaming", "Unused")
@Immutable
data class DesignSystemTypeScale internal constructor(
    private val _displayExtraXL: DsStaticTypeScale,
    private val _titleExtraL: DsStaticTypeScale,
    private val _textStrongM: DsStaticTypeScale,
    private val _textStrongS: DsStaticTypeScale,
    private val _textRegularM: DsStaticTypeScale,
    private val _textRegularS: DsStaticTypeScale,
    private val _textRegularXS: DsStaticTypeScale,
    private val _textExtraXS: DsStaticTypeScale,
) {
    val displayExtraXL: TextStyle @Composable get() = _displayExtraXL.textStyle
    val titleExtraL: TextStyle @Composable get() = _titleExtraL.textStyle
    val textStrongM: TextStyle @Composable get() = _textStrongM.textStyle
    val textStrongS: TextStyle @Composable get() = _textStrongS.textStyle
    val textRegularM: TextStyle @Composable get() = _textRegularM.textStyle
    val textRegularS: TextStyle @Composable get() = _textRegularS.textStyle
    val textRegularXS: TextStyle @Composable get() = _textRegularXS.textStyle
    val textExtraXS: TextStyle @Composable get() = _textExtraXS.textStyle

    @Composable
    fun withStringKey(typeScale: String): TextStyle =
        when (typeScale) {
            "displayExtraXL" -> DesignSystemThemeImpl.typeScale.displayExtraXL
            "titleExtraL" -> DesignSystemThemeImpl.typeScale.titleExtraL
            "textStrongM" -> DesignSystemThemeImpl.typeScale.textStrongM
            "textStrongS" -> DesignSystemThemeImpl.typeScale.textStrongS
            "textRegularM" -> DesignSystemThemeImpl.typeScale.textRegularM
            "textRegularS" -> DesignSystemThemeImpl.typeScale.textRegularS
            "textRegularXS" -> DesignSystemThemeImpl.typeScale.textRegularXS
            "textExtraXS" -> DesignSystemThemeImpl.typeScale.textExtraXS
            else -> DesignSystemThemeImpl.typeScale.textRegularS
        }
}

private val DsStaticTypeScale.textStyle: TextStyle
    @Composable get() =
        TextStyle(
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            textDecoration = textDecoration,
            fontSize = fontSize.textDp,
            lineHeight = lineHeight.textDp,
            letterSpacing = letterSpacing.em,
            fontFeatureSettings = fontFeatureSettings,
        )

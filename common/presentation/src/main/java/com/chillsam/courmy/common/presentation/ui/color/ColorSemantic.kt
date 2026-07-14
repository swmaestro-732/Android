package com.chillsam.courmy.common.presentation.ui.color

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

@Immutable
data class DesignSystemSemanticColors(
    val bgDefaultLevel0: Color,
    val bgDefaultLevel1: Color,
    val borderDefaultLevel0: Color,
    val contentDefaultLevel0: Color,
    val contentDefaultLevel1: Color,
    val contentDefaultLevel2: Color,
    val contentDefaultLevel3: Color,
    val contentOnAccent: Color,
    val contentAccent: Color,
    val contentRating: Color,
    val contentDanger: Color,
    val contentSuccess: Color,
    val contentLocation: Color,
) {
    @Composable
    fun withStringKey(key: String): Color =
        when (key) {
            "bgDefaultLevel0" -> DesignSystemThemeImpl.designSystemColor.bgDefaultLevel0
            "bgDefaultLevel1" -> DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1
            "borderDefaultLevel0" -> DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0
            "contentDefaultLevel0" -> DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0
            "contentDefaultLevel1" -> DesignSystemThemeImpl.designSystemColor.contentDefaultLevel1
            "contentDefaultLevel2" -> DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2
            "contentDefaultLevel3" -> DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3
            "contentOnAccent" -> DesignSystemThemeImpl.designSystemColor.contentOnAccent
            "contentAccent" -> DesignSystemThemeImpl.designSystemColor.contentAccent
            "contentRating" -> DesignSystemThemeImpl.designSystemColor.contentRating
            "contentDanger" -> DesignSystemThemeImpl.designSystemColor.contentDanger
            "contentSuccess" -> DesignSystemThemeImpl.designSystemColor.contentSuccess
            "contentLocation" -> DesignSystemThemeImpl.designSystemColor.contentLocation
            else -> DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3
        }
}

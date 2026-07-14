package com.chillsam.courmy.main.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.chillsam.courmy.common.presentation.component.ArchiText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/**
 * 기본 시작 화면.
 *
 * 레퍼런스 feature 제거 후 남는 빈 뼈대 화면이다. 새 feature 를 추가할 때 이 화면을
 * 대체하거나 여기서 진입점을 연결한다.
 */
@Composable
fun HomePage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        ArchiText(
            text = "Home",
            style = DesignSystemThemeImpl.typeScale.titleExtraL,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel1,
        )
    }
}

package com.chillsam.courmy.main.presentation.course

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.domain.my.MyPage

/** 코스 저장 완료 화면. "내 코스에서 보기" 로 마이 화면으로 이동한다. */
@Composable
fun CourseCompletePage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DsText(
            text = "코스가 완성됐어요 🎉",
            style = DesignSystemThemeImpl.typeScale.titleExtraL,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
        )
        DsText(
            text = "내 코스에 저장되었어요.",
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
            modifier = Modifier.padding(top = 8.dp),
        )
        DsButton(
            text = "내 코스에서 보기",
            onClick = { navigationHelper.navigateTo(MyPage) },
            modifier = Modifier.padding(top = 32.dp),
        )
    }
}

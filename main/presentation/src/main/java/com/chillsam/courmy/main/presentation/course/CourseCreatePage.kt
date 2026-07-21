package com.chillsam.courmy.main.presentation.course

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsButtonVariant
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.DsTextField
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.domain.course.CourseCompletePage

/**
 * 새 코스 만들기 화면. 네비게이션 검증용 최소 구현.
 * 코스 이름 입력 + 완료(→ 코스 완성) + 임시저장(→ 목록에 저장 후 뒤로).
 */
@Composable
fun CourseCreatePage(
    initialTitle: String = "",
    modifier: Modifier = Modifier,
) {
    val navigationHelper = LocalNavigationHelper.current
    var name by remember(initialTitle) { mutableStateOf(initialTitle) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DsText(
            text = "코스 만들기",
            style = DesignSystemThemeImpl.typeScale.titleExtraL,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
        )
        Spacer(Modifier.height(24.dp))
        DsTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = "코스 이름을 입력하세요",
        )
        Spacer(Modifier.height(16.dp))
        DsButton(
            text = "완료",
            enabled = name.isNotBlank(),
            onClick = {
                CourseSessionStore.addCourse(name.trim())
                navigationHelper.navigateTo(CourseCompletePage)
            },
        )
        Spacer(Modifier.height(12.dp))
        DsButton(
            text = "임시저장",
            variant = DsButtonVariant.Secondary,
            onClick = {
                CourseSessionStore.addDraft(name.trim().ifBlank { "제목 없는 코스" })
                navigationHelper.navigateToBack()
            },
        )
    }
}

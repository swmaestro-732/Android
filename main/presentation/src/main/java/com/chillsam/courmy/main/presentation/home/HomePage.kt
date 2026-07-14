package com.chillsam.courmy.main.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsBadge
import com.chillsam.courmy.common.presentation.component.DsBadgeStyle
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsButtonVariant
import com.chillsam.courmy.common.presentation.component.DsChip
import com.chillsam.courmy.common.presentation.component.DsSwitch
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.DsTextField
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/**
 * 기본 시작 화면 — 현재는 공용 디자인 컴포넌트(Ds*) 확인용 쇼케이스.
 * 실제 feature 화면을 붙일 때 이 내용을 대체한다.
 */
@Composable
fun HomePage(modifier: Modifier = Modifier) {
    var textValue by remember { mutableStateOf("") }
    var chipSelected by remember { mutableStateOf(true) }
    var switchOn by remember { mutableStateOf(true) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        DsText(
            text = "Design System",
            style = DesignSystemThemeImpl.typeScale.titleExtraL,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
        )

        SectionLabel("Button")
        DsButton(text = "따라가기", onClick = {})
        DsButton(text = "편집", variant = DsButtonVariant.Secondary, onClick = {})
        DsButton(text = "따라가기", loading = true, onClick = {})
        DsButton(text = "따라가기", enabled = false, onClick = {})

        SectionLabel("TextField (빈 값 · 입력됨 · 에러)")
        DsTextField(
            value = textValue,
            onValueChange = { textValue = it },
            placeholder = "코스 이름 입력 (빈 값 = 회색)",
        )
        DsTextField(
            value = "성수 비 오는 날 (입력됨 = 흰색)",
            onValueChange = {},
        )
        DsTextField(
            value = "",
            onValueChange = {},
            placeholder = "이름을 입력해 주세요",
            isError = true,
        )

        SectionLabel("Chip")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DsChip(text = "카페", selected = chipSelected, onClick = { chipSelected = !chipSelected })
            DsChip(text = "전시", selected = false, onClick = {})
            DsChip(text = "맛집", selected = false, enabled = false, onClick = {})
        }

        SectionLabel("Switch")
        DsSwitch(checked = switchOn, onCheckedChange = { switchOn = it })

        SectionLabel("Badge")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DsBadge(text = "영업 중", style = DsBadgeStyle.Success, showDot = true)
            DsBadge(text = "미방문", style = DsBadgeStyle.Accent)
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    DsText(
        text = text,
        style = DesignSystemThemeImpl.typeScale.textExtraXS,
        color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
    )
}

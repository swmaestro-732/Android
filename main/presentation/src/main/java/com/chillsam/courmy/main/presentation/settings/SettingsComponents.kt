package com.chillsam.courmy.main.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding

/** 설정 계열 목록의 행 좌우 여백. 구분선은 전체 폭으로 그어 행끼리만 나눈다. */
private val RowHorizontalPadding = ScreenHorizontalPadding

/** 목록 행 사이 구분선. */
@Composable
internal fun SettingsDivider() {
    HorizontalDivider(
        thickness = 1.dp,
        color = DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0,
    )
}

/**
 * 상세 화면으로 들어가는 목록 행. 오른쪽 chevron 으로 진입을 표시한다.
 * [hint] 는 chevron 앞에 흐린 색으로 붙는 보조 문구다(예: 아직 화면이 없는 항목의 "(준비 중)").
 */
@Composable
internal fun SettingsMenuRow(
    label: String,
    onClick: () -> Unit,
    hint: String? = null,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = RowHorizontalPadding, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsText(
            text = label,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = if (hint == null) color.contentDefaultLevel0 else color.contentDefaultLevel2,
            modifier = Modifier.weight(1f),
        )
        hint?.let {
            DsText(
                text = it,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel3,
                modifier = Modifier.padding(end = 4.dp),
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right_24),
            contentDescription = null,
            tint = color.contentDefaultLevel3,
            modifier = Modifier.size(22.dp),
        )
    }
}

/** 파괴적 액션 행(로그아웃·회원 탈퇴). danger 색 텍스트. */
@Composable
internal fun SettingsDangerRow(
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = RowHorizontalPadding, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsText(
            text = label,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = DesignSystemThemeImpl.designSystemColor.contentDanger,
        )
    }
}

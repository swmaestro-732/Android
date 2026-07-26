package com.chillsam.courmy.main.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsSwitch
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.domain.my.ProfileEditPage
import com.chillsam.courmy.main.presentation.component.BackTopBar

/** 설정 화면(FS-28). 프로필·알림·계정 관리. */
@Composable
fun SettingsPage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor
    var pushOn by remember { mutableStateOf(true) }
    var recommendOn by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        BackTopBar(title = "설정", onBack = { navigationHelper.navigateToBack() })

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
        ) {
            ProfileRow(onEdit = { navigationHelper.navigateTo(ProfileEditPage) })

            SectionLabel("알림")
            ToggleRow(label = "푸시 알림", checked = pushOn, onCheckedChange = { pushOn = it })
            ToggleRow(label = "코스 추천 알림", checked = recommendOn, onCheckedChange = { recommendOn = it })

            SectionLabel("계정")
            MenuRow(label = "개인정보 보호", onClick = {})
            MenuRow(label = "공지·도움말", onClick = {})
            Row(
                modifier = Modifier.fillMaxWidth().clickable {}.padding(vertical = 16.dp),
            ) {
                DsText(
                    text = "로그아웃",
                    style = DesignSystemThemeImpl.typeScale.textRegularM,
                    color = color.contentDanger,
                )
            }
        }
    }
}

@Composable
private fun ProfileRow(onEdit: () -> Unit) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(color.borderDefaultLevel0))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            DsText(
                text = "홍지호",
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
            )
            DsText(
                text = "@jiho_routes",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
            )
        }
        Box(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .border(1.dp, color.borderDefaultLevel1, RoundedCornerShape(9999.dp))
                    .clickable(onClick = onEdit)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
        ) {
            DsText(
                text = "편집",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel1,
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    DsText(
        text = text,
        style = DesignSystemThemeImpl.typeScale.textRegularXS,
        color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
        modifier = Modifier.padding(top = 20.dp, bottom = 4.dp),
    )
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsText(
            text = label,
            style = DesignSystemThemeImpl.typeScale.textRegularM,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
            modifier = Modifier.weight(1f),
        )
        DsSwitch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun MenuRow(
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsText(
            text = label,
            style = DesignSystemThemeImpl.typeScale.textRegularM,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
            modifier = Modifier.weight(1f),
        )
        DsText(
            text = "›",
            style = DesignSystemThemeImpl.typeScale.textRegularM,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
        )
    }
}

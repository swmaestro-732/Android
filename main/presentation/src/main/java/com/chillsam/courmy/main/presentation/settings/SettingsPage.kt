package com.chillsam.courmy.main.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsSwitch
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.LocalSessionUiState
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.domain.my.ProfileEditPage
import com.chillsam.courmy.main.entity.my.MyProfileVO
import com.chillsam.courmy.main.presentation.component.BackTopBar

/** 설정 화면(FS-28). 프로필·알림·계정을 iOS식 그룹 카드로 구성한다. */
@Composable
fun SettingsPage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val session = LocalSessionUiState.current
    val color = DesignSystemThemeImpl.designSystemColor
    var pushOn by remember { mutableStateOf(true) }
    var recommendOn by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        BackTopBar(title = "설정", onBack = { navigationHelper.navigateToBack() }, boxed = true)

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(8.dp))
            SettingsCard {
                ProfileRow(onEdit = { navigationHelper.navigateTo(ProfileEditPage) })
            }

            SectionLabel("알림")
            SettingsCard {
                ToggleRow(label = "푸시 알림", checked = pushOn, onCheckedChange = { pushOn = it })
                CardDivider()
                ToggleRow(label = "코스 추천 알림", checked = recommendOn, onCheckedChange = { recommendOn = it })
            }

            SectionLabel("계정")
            SettingsCard {
                MenuRow(label = "개인정보 보호", onClick = {})
                CardDivider()
                MenuRow(label = "공지·도움말", onClick = {})
                CardDivider()
                LogoutRow(
                    onClick = {
                        session.logout()
                        navigationHelper.navigateTo(HomePage)
                    },
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

/** 흰색 라운드 + 테두리 그룹 카드. 내부 행 사이는 [CardDivider] 로 구분. */
@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(color.bgDefaultLevel1)
                .border(1.dp, color.borderDefaultLevel0, RoundedCornerShape(16.dp)),
    ) {
        content()
    }
}

@Composable
private fun CardDivider() {
    HorizontalDivider(
        thickness = 1.dp,
        color = DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0,
    )
}

@Composable
private fun ProfileRow(onEdit: () -> Unit) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(modifier = Modifier.size(54.dp).clip(CircleShape).background(color.imagePlaceholder))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            DsText(
                text = MyProfileVO.sample.nickname,
                style = DesignSystemThemeImpl.typeScale.textRegularM,
                color = color.contentDefaultLevel0,
            )
            DsText(
                text = "@${MyProfileVO.sample.handle}",
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
                    .padding(horizontal = 14.dp, vertical = 7.dp),
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
        modifier = Modifier.padding(start = 6.dp, top = 20.dp, bottom = 8.dp),
    )
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsText(
            text = label,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
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
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsText(
            text = label,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel0,
            modifier = Modifier.weight(1f),
        )
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right_24),
            contentDescription = null,
            tint = color.contentDefaultLevel3,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
private fun LogoutRow(onClick: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsText(
            text = "로그아웃",
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = DesignSystemThemeImpl.designSystemColor.contentDanger,
        )
    }
}
